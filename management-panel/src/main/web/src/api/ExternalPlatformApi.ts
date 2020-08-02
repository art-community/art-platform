import {createFunctionRequest, PlatformClient} from "../client/PlatformClient";
import {Dispatch} from "react";
import {AUTHORIZE} from "../constants/ApiConstants";
import {useNotifications} from "../framework/hooks/Hooks";
import {PlatformResource} from "../model/ResourceTypes";
import {User} from "../model/UserTypes";

export const useExternalPlatform = () => {
    const notifications = useNotifications();
    let client: PlatformClient | undefined = undefined;

    return {
        connect: (resource: PlatformResource, connected: Dispatch<PlatformClient>) => {
            client?.disposeRsocket();
            const authorizationRequest = createFunctionRequest(AUTHORIZE, {
                name: resource.userName,
                password: resource.password
            });
            client = PlatformClient.newPlatformClient(resource.url);
            client.connect(
                resource.name,
                () => client!.requestResponse(authorizationRequest, (user: User) => connected(client!.token(user.token))),
                error => notifications.error(error.message)
            );
        },
        close: () => client?.disposeRsocket(),
        client: () => client
    }
};