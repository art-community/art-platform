import {createFunctionRequest, requestResponse} from "../client/PlatformClient";
import {ADD_FILEBEAT_APPLICATION, DELETE_FILEBEAT_APPLICATION, GET_APPLICATIONS_IDS, GET_FILEBEAT_APPLICATION, GET_FILEBEAT_APPLICATIONS, UPDATE_FILEBEAT_APPLICATION} from "../constants/ApiConstants";
import {Dispatch, DispatchWithoutAction} from "react";
import {useApiActions} from "./ApiActions";
import {ApplicationIdentifier} from "../model/ApplicationTypes";
import {FilebeatApplication, FilebeatApplicationRequest} from "../model/FilebeatTypes";

export const useApplicationApi = () => {
    const actions = useApiActions();

    return {
        addFilebeatApplication: (request: FilebeatApplicationRequest, onComplete: DispatchWithoutAction) => {
            requestResponse(createFunctionRequest(ADD_FILEBEAT_APPLICATION, request), onComplete, actions.errorHandler())
        },

        getFilebeatApplications: (onComplete: Dispatch<FilebeatApplication[]>) => {
            requestResponse(createFunctionRequest(GET_FILEBEAT_APPLICATIONS), onComplete, actions.errorHandler())
        },

        getFilebeatApplication: (id: number, onComplete: Dispatch<FilebeatApplication>) => {
            requestResponse(createFunctionRequest(GET_FILEBEAT_APPLICATION, id), onComplete, actions.errorHandler())
        },

        getApplicationIds: (onComplete: Dispatch<ApplicationIdentifier[]>) => {
            requestResponse(createFunctionRequest(GET_APPLICATIONS_IDS), onComplete, actions.errorHandler())
        },

        updateFilebeatApplication: (application: FilebeatApplication, onComplete: Dispatch<FilebeatApplication>) => {
            requestResponse(createFunctionRequest(UPDATE_FILEBEAT_APPLICATION, application), onComplete, actions.errorHandler())
        },

        deleteFilebeatApplication: (id: number, onComplete: Dispatch<FilebeatApplication>) => {
            requestResponse(createFunctionRequest(DELETE_FILEBEAT_APPLICATION, id), onComplete)
        }
    };
};
