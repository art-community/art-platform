import {createFunctionRequest, infinityRequestStream, requestResponse} from "../client/PlatformClient";
import {AUTHENTICATE, AUTHORIZE, DELETE_USER, GET_USER, GET_USER_EMAILS, GET_USER_NAMES, GET_USERS, REGISTER_USER, SUBSCRIBE_ON_USER, UPDATE_USER} from "../constants/ApiConstants";
import {Dispatch, DispatchWithoutAction} from "react";
import {useApiActions} from "./ApiActions";
import {ServiceExceptionDispatch} from "../model/ApiTypes";
import {User, UserAuthorizationRequest, UserRegistrationRequest} from "../model/UserTypes";
import {doNothing} from "../framework/constants/Constants";
import {StreamEvent} from "../framework/pattern/Stream";

export const authenticate = (token: string, onSuccess: Dispatch<User>, onError: DispatchWithoutAction) => {
    const request = createFunctionRequest(AUTHENTICATE, token);
    requestResponse(request, authenticated => authenticated ? onSuccess(authenticated) : onError(), onError)
};

export const useUserApi = () => {
    const actions = useApiActions();

    return {
        authorize: (requestData: UserAuthorizationRequest, onComplete: Dispatch<User>, onError: ServiceExceptionDispatch) => {
            requestResponse(createFunctionRequest(AUTHORIZE, requestData), onComplete, actions.errorHandler(onError))
        },
        registerUser: (requestData: UserRegistrationRequest, onComplete: Dispatch<User>, onError: ServiceExceptionDispatch) => {
            requestResponse(createFunctionRequest(REGISTER_USER, requestData), onComplete, actions.errorHandler(onError));
        },
        getUserNames: (onComplete: Dispatch<string[]>) => {
            requestResponse(createFunctionRequest(GET_USER_NAMES), onComplete, actions.errorHandler());
        },
        getUser: (id: number, onComplete: Dispatch<User>) => {
            requestResponse(createFunctionRequest(GET_USER, id), onComplete, actions.errorHandler());
        },
        getUsers: (onComplete: Dispatch<User[]>) => {
            requestResponse(createFunctionRequest(GET_USERS), onComplete, actions.errorHandler());
        },
        getUserEmails: (onComplete: Dispatch<string[]>) => {
            requestResponse(createFunctionRequest(GET_USER_EMAILS), onComplete, actions.errorHandler());
        },
        updateUser: (user: User, onComplete: Dispatch<User> = doNothing) => {
            requestResponse(createFunctionRequest(UPDATE_USER, user), onComplete, actions.errorHandler());
        },
        deleteUser: (id: number, onComplete: Dispatch<User>) => {
            requestResponse(createFunctionRequest(DELETE_USER, id), onComplete, actions.errorHandler());
        },
        subscribeOnUser: (onUpdate: Dispatch<StreamEvent<User>>) => {
            return infinityRequestStream(createFunctionRequest(SUBSCRIBE_ON_USER), onUpdate, actions.errorHandler());
        }
    };
};
