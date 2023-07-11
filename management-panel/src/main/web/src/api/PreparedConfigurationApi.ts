import {createFunctionRequest, requestResponse} from "../client/PlatformClient";
import {Dispatch, DispatchWithoutAction} from "react";
import {
    ADD_PREPARED_CONFIGURATION,
    DELETE_PREPARED_CONFIGURATION,
    GET_FILTERED_PREPARED_CONFIGURATIONS,
    GET_PREPARED_CONFIGURATION,
    GET_PREPARED_CONFIGURATION_IDS,
    GET_PREPARED_CONFIGURATIONS,
    UPDATE_PREPARED_CONFIGURATION
} from "../constants/ApiConstants";
import {useApiActions} from "./ApiActions";
import {PreparedConfiguration, PreparedConfigurationFilterCriteria, PreparedConfigurationIdentifier, PreparedConfigurationRequest} from "../model/PreparedConfigurationTypes";

export const usePreparedConfigurationApi = () => {
    const actions = useApiActions();

    return {
        addPreparedConfiguration: (request: PreparedConfigurationRequest, onComplete: DispatchWithoutAction) => {
            requestResponse(createFunctionRequest(ADD_PREPARED_CONFIGURATION, request), onComplete, actions.errorHandler())
        },

        getPreparedConfigurations: (onComplete: Dispatch<PreparedConfiguration[]>) => {
            requestResponse(createFunctionRequest(GET_PREPARED_CONFIGURATIONS), onComplete, actions.errorHandler())
        },

        getFilteredPreparedConfigurations: (request: PreparedConfigurationFilterCriteria, onComplete: Dispatch<PreparedConfigurationIdentifier[]>) => {
            requestResponse(createFunctionRequest(GET_FILTERED_PREPARED_CONFIGURATIONS, request), onComplete, actions.errorHandler())
        },

        getPreparedConfiguration: (id: number, onComplete: Dispatch<PreparedConfiguration>) => {
            requestResponse(createFunctionRequest(GET_PREPARED_CONFIGURATION, id), onComplete, actions.errorHandler())
        },

        getPreparedConfigurationIds: (onComplete: Dispatch<PreparedConfigurationIdentifier[]>) => {
            requestResponse(createFunctionRequest(GET_PREPARED_CONFIGURATION_IDS), onComplete, actions.errorHandler())
        },

        updatePreparedConfiguration: (configuration: PreparedConfiguration, onComplete: Dispatch<PreparedConfiguration>) => {
            requestResponse(createFunctionRequest(UPDATE_PREPARED_CONFIGURATION, configuration), onComplete, actions.errorHandler())
        },

        deletePreparedConfiguration: (id: number, onComplete: Dispatch<PreparedConfiguration>) => {
            requestResponse(createFunctionRequest(DELETE_PREPARED_CONFIGURATION, id), onComplete)
        },
    };
};
