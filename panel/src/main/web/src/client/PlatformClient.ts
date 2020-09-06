import {BufferEncoders, RSocketClient} from "rsocket-core";
import RSocketWebSocketClient from "rsocket-websocket-client";
import {decode, encode} from "msgpack-lite";
import Cookies from "js-cookie";
import {ISubscription, Payload, ReactiveSocket} from "rsocket-types";
import {
    apiLogsEnabled,
    INFINITY_STREAM_RSOCKET_CLIENT_NAME,
    RETRY_TIMEOUT_SECONDS,
    RSOCKET_DEFAULT_URL,
    RSOCKET_FUNCTION,
    RSOCKET_OPTIONS,
    RSOCKET_REQUEST_COUNT,
    STREAM_RSOCKET_CLIENT_NAME
} from "../constants/ApiConstants";
import {Flowable} from "rsocket-flowable";
import {Dispatch, DispatchWithoutAction} from "react";
import moment from "moment";
import {isEmptyArray} from "../framework/extensions/extensions";
import {setInterval} from "timers";
import {ConnectionStatus} from "rsocket-types/ReactiveSocketTypes";
import {ServiceExceptionDispatch, ServiceExecutionException, ServiceResponse} from "../model/ApiTypes";
import {doNothing} from "../framework/constants/Constants";
import {TOKEN_COOKIE} from "../constants/Cookies";
import {DATE_TIME_FORMAT} from "../constants/DateTimeConstants";
import {development} from "../constants/Environment";

export const createServiceMethodRequest = (serviceId: string, methodId: string, requestData: any = null) => ({
    serviceMethodCommand: {
        serviceId: serviceId,
        methodId: methodId,
        toString: () => `${serviceId}.${methodId}`
    },
    requestData: requestData
});

export const createFunctionRequest = (methodId: string, requestData: any = null) => ({
    serviceMethodCommand: {
        serviceId: RSOCKET_FUNCTION,
        methodId: methodId,
        toString: () => `${RSOCKET_FUNCTION}.${methodId}`
    },
    requestData: requestData
});

const clients: PlatformClient[] = []

export class PlatformClient {
    protected static INSTANCE: PlatformClient;
    #subscriptions = new Set<ISubscription>();
    #rsocketUrl: string;
    #connected: DispatchWithoutAction = doNothing;
    #failed: Dispatch<Error> = doNothing;
    #rsocket?: ReactiveSocket<any, any>;
    #name?: string;
    #defaultToken?: string;
    #reconnectionInterval?: NodeJS.Timeout;
    #connectionStatus: ConnectionStatus = {kind: "NOT_CONNECTED"};

    private constructor(rsocketUrl: string) {
        this.#rsocketUrl = rsocketUrl;
        clients.push(this);
    }

    token = (token: string) => {
        this.#defaultToken = token;
        return this;
    };

    connect = (name: string, connected: DispatchWithoutAction, failed: Dispatch<Error>) => {
        if (this.#connectionStatus.kind != "NOT_CONNECTED") {
            return
        }
        this.#name = name;
        this.#connected = connected;
        this.#failed = failed;
        this.#processConnection(name, connected, failed);
    };

    disposeRsocket = () => {
        try {
            this.#subscriptions.forEach(subscription => {
                try {
                    subscription.cancel()
                } catch (ignored) {
                    console.warn(ignored)
                }
            });
            this.#subscriptions.clear();
            if (this.#name) {
                console.log(`${this.#name} subscriptions canceled`);
            }
            this.#closeRsocket()
        } catch (ignored) {
            console.warn(ignored)
        }
    };

    requestResponse = (request: any, onSuccess: Dispatch<any> = doNothing, onError: Dispatch<any> = doNothing) => {
        const command = request.serviceMethodCommand;
        const token = this.#defaultToken || Cookies.get(TOKEN_COOKIE);
        const metadata = createFunctionRequest(command.methodId, token);
        const payload = {
            data: encode(request),
            metadata: encode(metadata)
        };

        this.#rsocket!.requestResponse(payload)
        .map(this.#writePayload)
        .subscribe({
            onComplete: (response: ServiceResponse) => {
                this.#log(() => `[requestResponse(${command}): ${this.#rsocketUrl}]: Done at ${moment().format(DATE_TIME_FORMAT)}`);
                this.#onResponse(response, onSuccess, onError);
            },
            onError: (error: Error) => {
                console.error(error);
                onError(error)
            },
            onSubscribe: () => {
                this.#log(() => `[requestResponse(${command}): ${this.#rsocketUrl}]: Start at: ${moment().format(DATE_TIME_FORMAT)}`);
            }
        })
    };

    infinityRequestStream = (request: any, onNext: Dispatch<any> = doNothing, onError: Dispatch<any> = doNothing) => this.requestStream(request, onNext, doNothing, onError);

    requestStream = (request: any, onNext: Dispatch<any> = doNothing, onComplete: DispatchWithoutAction = doNothing, onError: Dispatch<any> = doNothing) => {
        let subscription: ISubscription | null;
        const command = request.serviceMethodCommand;
        const token = this.#defaultToken || Cookies.get(TOKEN_COOKIE);
        const metadata = createFunctionRequest(command.methodId, token);
        const payload = {
            data: encode(request),
            metadata: encode(metadata)
        };

        this.#rsocket!.requestStream(payload)
        .map(this.#writePayload)
        .subscribe({
                onComplete: () => {
                    this.#log(() => `[requestStream(${command}): ${this.#rsocketUrl}]: Complete at: ${moment().format(DATE_TIME_FORMAT)}`);
                    if (subscription) {
                        this.#subscriptions.delete(subscription);
                        subscription = null;
                    }
                    onComplete();
                },
                onError: (error: Error) => {
                    console.error(error);
                    if (subscription) {
                        this.#subscriptions.delete(subscription);
                        subscription = null;
                    }
                    onError(error)
                },
                onNext: (response: ServiceResponse) => {
                    this.#log(() => `[requestStream(${command}): ${this.#rsocketUrl}]: Event at ${moment().format(DATE_TIME_FORMAT)}`);
                    this.#onResponse(response, onNext, onError)
                },
                onSubscribe: (currentSubscription: ISubscription) => {
                    this.#log(() => `[requestStream(${command}): ${this.#rsocketUrl}]: Start at: ${moment().format(DATE_TIME_FORMAT)}`);
                    subscription = currentSubscription;
                    this.#subscriptions.add(subscription);
                    subscription.request(RSOCKET_REQUEST_COUNT);
                }
            }
        );

        return () => {
            if (!subscription) {
                return;
            }
            subscription.cancel();
            this.#subscriptions.delete(subscription);
            subscription = null;
        }
    };

    chunkedRequest = (chunks: any[], onComplete: DispatchWithoutAction = doNothing, onError: Dispatch<any> = doNothing) => {
        const chunkValues = chunks;
        let subscription: ISubscription | null;

        const flowable = new Flowable<Payload<any, any>>(subscriber => subscriber.onSubscribe({
            cancel: doNothing,
            request: count => {
                while (count--) {
                    if (isEmptyArray(chunkValues)) {
                        subscriber.onComplete();
                        return
                    }
                    const next = chunkValues.shift();
                    const token = this.#defaultToken || Cookies.get(TOKEN_COOKIE);
                    const metadata = createFunctionRequest(next!.serviceMethodCommand.methodId, token);
                    subscriber.onNext({
                        data: encode(next),
                        metadata: encode(metadata)
                    });
                }
            }
        }));

        (this.#rsocket!.requestChannel(flowable).map(this.#writePayload) as Flowable<any>)
        .subscribe({
                onComplete: () => {
                    if (subscription) {
                        this.#subscriptions.delete(subscription);
                        subscription = null;
                    }
                    onComplete()
                },
                onError: (error: Error) => {
                    console.error(error);
                    if (subscription) {
                        this.#subscriptions.delete(subscription);
                        subscription = null;
                    }
                    onError(error)
                },
                onSubscribe: (currentSubscription: ISubscription) => {
                    subscription = currentSubscription;
                    this.#subscriptions.add(subscription);
                    subscription.request(chunkValues.length);
                }
            }
        );

        return () => {
            if (!subscription) {
                return;
            }
            subscription.cancel();
            this.#subscriptions.delete(subscription);
            subscription = null;
        }
    };

    fireAndForget = (request: any) => {
        const command = request.serviceMethodCommand;
        this.#log(() => `[fireAndForget(${command}): ${this.#rsocketUrl}]: Executed at: ${moment().format(DATE_TIME_FORMAT)}`);
        const token = this.#defaultToken || Cookies.get(TOKEN_COOKIE);
        const metadata = createFunctionRequest(command.methodId, token);
        const payload = {
            data: encode(request),
            metadata: encode(metadata)
        };
        this.#rsocket!.fireAndForget(payload)
    };

    #writePayload = (payload: any) => payload.data ? decode(payload.data as number[]) : null;

    #processConnection = (name: string, connected: () => void, failed: (value: Error) => void) => {
        new RSocketClient({
            setup: RSOCKET_OPTIONS,
            transport: new RSocketWebSocketClient({url: this.#rsocketUrl}, BufferEncoders)
        })
        .connect()
        .then(socket => {
            console.log(`${name} successfully connected to ${this.#rsocketUrl}`);
            this.#rsocket = socket;
            socket.connectionStatus().subscribe({
                onSubscribe: (subscription: ISubscription) => subscription.request(RSOCKET_REQUEST_COUNT),
                onNext: status => this.#connectionStatus = status,
                onError: error => this.#connectionStatus = {kind: "ERROR", error: error},
                onComplete: () => this.#connectionStatus = {kind: "CLOSED"}
            });
            this.#startConnectionListening();
            connected();
        }, error => {
            console.error(error);
            this.#connectionStatus = {kind: "ERROR", error: error};
            this.#startConnectionListening();
            failed(error)
        });
    };

    #startConnectionListening = () => {
        if (this.#reconnectionInterval) {
            return;
        }
        const connected = () => {
            this.#reconnectionInterval && clearInterval(this.#reconnectionInterval);
            window.location.reload()
        };
        this.#reconnectionInterval = setInterval(() => {
            switch (PlatformClient.platformClient().#connectionStatus.kind) {
                case "NOT_CONNECTED":
                    console.log(`${this.#name} trying reconnect to ${this.#rsocketUrl}...`);
                    this.#processConnection(this.#name!, connected, this.#failed);
                    break;
                case "CLOSED":
                    console.log(`${this.#name} trying reconnect to ${this.#rsocketUrl}...`);
                    this.#processConnection(this.#name!, connected, this.#failed);
                    break;
                case "ERROR":
                    console.log(`${this.#name} trying reconnect to ${this.#rsocketUrl}...`);
                    this.#processConnection(this.#name!, connected, this.#failed);
                    break;
            }
        }, RETRY_TIMEOUT_SECONDS * 1000);
    };

    #onResponse = (response: ServiceResponse, onSuccess: Dispatch<any>, onError: ServiceExceptionDispatch) => {
        const exception = response.serviceExecutionException;
        if (!exception) {
            onSuccess(response.responseData);
            return;
        }
        console.error(exception);
        const {errorCode, errorMessage, stackTrace} = response.serviceExecutionException;
        onError(new ServiceExecutionException(errorCode, errorMessage, stackTrace));
    };

    #closeRsocket = () => {
        if (!this.#rsocket) {
            return;
        }
        try {
            this.#rsocket.close();
            console.log(`${this.#name} successfully disconnected from ${this.#rsocketUrl}`);
        } catch (exception) {
            console.warn(exception);
        }
    };

    #log = (message: () => string) => {
        if (development() && apiLogsEnabled()) {
            console.log(message());
        }
    };

    static platformClient = (): PlatformClient => {
        PlatformClient.INSTANCE = PlatformClient.INSTANCE || new PlatformClient(RSOCKET_DEFAULT_URL);
        return PlatformClient.INSTANCE;
    };

    static newPlatformClient = (url: string = RSOCKET_DEFAULT_URL): PlatformClient => new PlatformClient(url);
}

export const requestResponse = (request: any, onComplete: Dispatch<any> = doNothing, onError: Dispatch<any> = doNothing) =>
    PlatformClient.platformClient().requestResponse(request, onComplete, onError);

export const requestStream = (request: any, onNext: Dispatch<any> = doNothing, onComplete: DispatchWithoutAction = doNothing, onError: Dispatch<any> = doNothing) => {
    const client = PlatformClient.newPlatformClient();
    let stream;
    client.connect(STREAM_RSOCKET_CLIENT_NAME, () => stream = client.requestStream(request, onNext, onComplete, onError), onError);
    return () => {
        stream?.();
        client.disposeRsocket();
    }
}

export const infinityRequestStream = (request: any, onNext: Dispatch<any> = doNothing, onError: Dispatch<any> = doNothing) => {
    const client = PlatformClient.newPlatformClient();
    let stream;
    client.connect(INFINITY_STREAM_RSOCKET_CLIENT_NAME, () => stream = client.infinityRequestStream(request, onNext, onError), onError);
    return () => {
        stream?.();
        client.disposeRsocket();
    }
}

export const chunkedRequest = (chunks: any[], onComplete: DispatchWithoutAction = doNothing, onError: Dispatch<any> = doNothing) =>
    PlatformClient.platformClient().chunkedRequest(chunks, onComplete, onError);

export const fireAndForget = (request: any) =>
    PlatformClient.platformClient().fireAndForget(request);

export const disposeRsockets = () => clients.forEach(client => client.disposeRsocket());
