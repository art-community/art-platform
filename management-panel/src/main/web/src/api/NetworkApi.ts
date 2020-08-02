import {createFunctionRequest, requestResponse} from "../client/PlatformClient";
import {CHECK_NETWORK_ACCESS} from "../constants/ApiConstants";
import {Dispatch} from "react";
import {useApiActions} from "./ApiActions";
import {ServiceExceptionDispatch} from "../model/ApiTypes";
import {NetworkAccessRequest} from "../model/NetworkTypes";

export const useNetworkApi = () => {
    const actions = useApiActions();

    return {
        checkNetworkAccess: (request: NetworkAccessRequest, onComplete: Dispatch<boolean>, onError: ServiceExceptionDispatch) => {
            requestResponse(createFunctionRequest(CHECK_NETWORK_ACCESS, request), onComplete, actions.errorHandler(onError))
        },
    };
};