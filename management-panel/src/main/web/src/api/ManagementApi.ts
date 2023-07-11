import {createFunctionRequest, requestResponse} from "../client/PlatformClient";
import {GET_VERSION} from "../constants/ApiConstants";
import {Dispatch} from "react";
import {useApiActions} from "./ApiActions";

export const useManagementApi = () => {
    const actions = useApiActions();

    return {
        getVersion: (onComplete: Dispatch<string>) => {
            requestResponse(createFunctionRequest(GET_VERSION), onComplete, actions.errorHandler())
        }
    };
};