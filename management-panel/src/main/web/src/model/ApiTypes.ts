import {Dispatch} from "react";

export type ServiceExceptionDispatch = Dispatch<ServiceExecutionException>;

export type ServiceResponse = {
    serviceMethodCommand: ServiceMethodCommand
    responseData: any
    serviceExecutionException: ServiceExecutionException
}

export class ServiceExecutionException {
    errorCode: string;
    errorMessage: string;
    stackTrace: string;
    constructor(errorCode: string, errorMessage: string, stackTrace: string) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.stackTrace = stackTrace;
    }
}

export type ServiceMethodCommand = {
    serviceId: string
    methodId: string
}
