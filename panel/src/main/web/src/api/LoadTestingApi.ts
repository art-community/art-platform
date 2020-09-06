import {createFunctionRequest, infinityRequestStream, requestResponse} from "../client/PlatformClient";
import {
    CANCEL_LOAD_TEST,
    DELETE_LOAD_TEST,
    DELETE_LOAD_TEST_SCENARIO,
    GET_LOAD_TEST,
    GET_LOAD_TEST_SCENARIO,
    GET_PROJECTS_LOAD_TEST_SCENARIOS,
    GET_PROJECTS_LOAD_TESTS,
    SAVE_LOAD_TEST_SCENARIO,
    START_LOAD_TEST,
    SUBSCRIBE_ON_LOAD_TEST,
    UPDATE_LOAD_TEST_SCENARIO
} from "../constants/ApiConstants";
import {Dispatch} from "react";
import {useApiActions} from "./ApiActions";
import {StreamEvent} from "../framework/pattern/Stream";
import {ServiceExceptionDispatch} from "../model/ApiTypes";
import {LoadTest, LoadTestRequest, LoadTestScenario, LoadTestScenarioRequest} from "../model/LoadTestingTypes";
import {doNothing} from "../framework/constants/Constants";

export const useLoadTestingApi = () => {
    const actions = useApiActions();

    return {
        startLoadTest: (request: LoadTestRequest) => {
            requestResponse(createFunctionRequest(START_LOAD_TEST, request), doNothing, actions.errorHandler());
        },
        saveLoadTestScenario: (request: LoadTestScenarioRequest, onComplete: Dispatch<LoadTestScenario>, onError: ServiceExceptionDispatch) => {
            requestResponse(createFunctionRequest(SAVE_LOAD_TEST_SCENARIO, request), onComplete, actions.errorHandler(onError))
        },
        updateLoadTestScenario: (request: LoadTestScenario, onComplete: Dispatch<LoadTestScenario>, onError: ServiceExceptionDispatch) => {
            requestResponse(createFunctionRequest(UPDATE_LOAD_TEST_SCENARIO, request), onComplete, actions.errorHandler(onError))
        },
        getLoadTestScenario: (request: number, onComplete: Dispatch<LoadTestScenario>) => {
            requestResponse(createFunctionRequest(GET_LOAD_TEST_SCENARIO, request), onComplete, actions.errorHandler())
        },
        getProjectsLoadTestScenarios: (request: number, onComplete: Dispatch<LoadTestScenario[]>) => {
            requestResponse(createFunctionRequest(GET_PROJECTS_LOAD_TEST_SCENARIOS, request), onComplete, actions.errorHandler())
        },
        getProjectsLoadTests: (request: number, onComplete: Dispatch<LoadTest[]>) => {
            requestResponse(createFunctionRequest(GET_PROJECTS_LOAD_TESTS, request), onComplete, actions.errorHandler())
        },
        getLoadTest: (request: number, onComplete: Dispatch<LoadTest>) => {
            requestResponse(createFunctionRequest(GET_LOAD_TEST, request), onComplete, actions.errorHandler())
        },
        deleteLoadTest: (request: number) => {
            requestResponse(createFunctionRequest(DELETE_LOAD_TEST, request))
        },
        deleteLoadTestScenario: (request: number, onComplete: Dispatch<LoadTestScenario>) => {
            requestResponse(createFunctionRequest(DELETE_LOAD_TEST_SCENARIO, request), onComplete)
        },
        subscribeOnLoadTest: (onUpdate: Dispatch<StreamEvent<LoadTest>>) => {
            return infinityRequestStream(createFunctionRequest(SUBSCRIBE_ON_LOAD_TEST), onUpdate, actions.errorHandler);
        },
        cancelLoadTest: (request: number) => {
            requestResponse(createFunctionRequest(CANCEL_LOAD_TEST, request), doNothing, actions.errorHandler())
        },
    };
};
