package ru.art.platform.configuration

import ru.art.config.extensions.ConfigExtensions.*
import ru.art.core.factory.CollectionsFactory.*
import ru.art.platform.common.exception.*
import ru.art.platform.constants.ConfigKeys.ENABLE_SERVICE_LOGGING_KEY
import ru.art.service.*
import ru.art.service.ServiceModuleConfiguration.*
import ru.art.service.factory.ServiceResponseFactory.*
import ru.art.service.interceptor.*
import ru.art.service.interceptor.ServiceExecutionInterceptor.*
import ru.art.service.model.*
import ru.art.service.model.ServiceInterceptionResult.*

class ServiceConfiguration : ServiceModuleDefaultConfiguration() {
    override fun getRequestInterceptors(): MutableList<RequestInterceptor> {
        return linkedListOf(interceptRequest(object : ServiceLoggingInterception() {
            override fun intercept(request: ServiceRequest<*>): ServiceInterceptionResult {
                if (configBoolean(ENABLE_SERVICE_LOGGING_KEY)) {
                    return super.intercept(request)
                }
                return nextInterceptor(request)
            }
        }), interceptRequest(ServiceValidationInterception()))
    }


    override fun getResponseInterceptors(): MutableList<ResponseInterceptor> {
        return linkedListOf(interceptResponse(object : ServiceLoggingInterception() {
            override fun intercept(request: ServiceRequest<*>): ServiceInterceptionResult {
                if (configBoolean(ENABLE_SERVICE_LOGGING_KEY)) {
                    return super.intercept(request)
                }
                return nextInterceptor(request)
            }

            override fun intercept(request: ServiceRequest<*>, response: ServiceResponse<*>): ServiceInterceptionResult {
                if (configBoolean(ENABLE_SERVICE_LOGGING_KEY)) {
                    return super.intercept(request, response)
                }
                response.serviceException?.let {
                    return super.intercept(request, response)
                }
                return nextInterceptor(request, response)
            }
        }))
    }

    override fun getExceptionWrapper(): ServiceExecutionExceptionWrapper {
        return super.getExceptionWrapper().addExceptionWrapper(object : ServiceExecutionExceptionWrapper() {
            override fun <RequestType : Any?, ResponseType : Any?> wrapServiceExecution(command: ServiceMethodCommand?, request: RequestType): ServiceResponse<ResponseType> {
                return try {
                    previousWrapper.wrapServiceExecution(command, request)
                } catch (exception: PlatformException) {
                    errorResponse(command, exception.code, exception)
                }
            }
        })
    }
}