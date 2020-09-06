import {createFunctionRequest, infinityRequestStream, requestResponse} from "../client/PlatformClient";
import {ADD_LOG_RECORD, DELETE_LOG, GET_LOG, GET_LOGS, SUBSCRIBE_ON_LOG, UPDATE_LOG} from "../constants/ApiConstants";
import {Dispatch} from "react";
import {useApiActions} from "./ApiActions";
import {ServiceExceptionDispatch} from "../model/ApiTypes";
import {StreamEvent} from "../framework/pattern/Stream";
import {Log, LogRecordRequest} from "../model/LogTypes";


export const useLogApi = () => {
    const actions = useApiActions();
    return {
        addLogRecord: (request: LogRecordRequest, onComplete: Dispatch<Log>, onError: ServiceExceptionDispatch) => {
            requestResponse(createFunctionRequest(ADD_LOG_RECORD, request), onComplete, actions.errorHandler(onError))
        },
        updateLog: (request: Log, onComplete: Dispatch<Log>, onError: ServiceExceptionDispatch) => {
            requestResponse(createFunctionRequest(UPDATE_LOG, request), onComplete, actions.errorHandler(onError))
        },
        deleteLog: (id: number, onComplete: Dispatch<Log>, onError: ServiceExceptionDispatch) => {
            requestResponse(createFunctionRequest(DELETE_LOG, id), onComplete, actions.errorHandler(onError))
        },
        getLog: (id: number, onComplete: Dispatch<Log>) => {
            requestResponse(createFunctionRequest(GET_LOG, id), onComplete, actions.errorHandler())
        },
        getLogs: (onComplete: Dispatch<Log[]>, onError: ServiceExceptionDispatch) => {
            requestResponse(createFunctionRequest(GET_LOGS), onComplete, actions.errorHandler(onError))
        },
        subscribeOnLog: (onUpdate: Dispatch<StreamEvent<Log>>) =>
            infinityRequestStream(createFunctionRequest(SUBSCRIBE_ON_LOG), onUpdate, actions.errorHandler())
    };
};
