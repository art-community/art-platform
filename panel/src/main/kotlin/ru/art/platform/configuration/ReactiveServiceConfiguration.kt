package ru.art.platform.configuration;

import ru.art.config.extensions.ConfigExtensions.configBoolean
import ru.art.core.factory.CollectionsFactory.linkedListOf
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.constants.ConfigKeys.ENABLE_SERVICE_LOGGING_KEY
import ru.art.reactive.service.configuration.ReactiveServiceModuleConfiguration.ReactiveServiceModuleDefaultConfiguration
import ru.art.reactive.service.interception.ReactiveServiceLoggingInterception
import ru.art.reactive.service.interception.ReactiveServiceValidationInterception
import ru.art.reactive.service.wrapper.ReactiveServiceExceptionWrappers
import ru.art.service.exception.ServiceExecutionException
import ru.art.service.interceptor.ServiceExecutionInterceptor.*
import ru.art.service.model.ServiceInterceptionResult
import ru.art.service.model.ServiceInterceptionResult.nextInterceptor
import ru.art.service.model.ServiceRequest
import ru.art.service.model.ServiceResponse

class ReactiveServiceConfiguration : ReactiveServiceModuleDefaultConfiguration() {
    override fun getRequestInterceptors(): MutableList<RequestInterceptor> {
        return linkedListOf(interceptRequest(object : ReactiveServiceLoggingInterception() {
            override fun intercept(request: ServiceRequest<*>): ServiceInterceptionResult {
                if (configBoolean(ENABLE_SERVICE_LOGGING_KEY)) {
                    return super.intercept(request)
                }
                return nextInterceptor(request)
            }
        }), interceptRequest(ReactiveServiceValidationInterception()))
    }

    override fun getResponseInterceptors(): MutableList<ResponseInterceptor> {
        return linkedListOf(interceptResponse(object : ReactiveServiceLoggingInterception() {
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

    override fun getReactiveServiceExceptionWrappers(): ReactiveServiceExceptionWrappers {
        return super.getReactiveServiceExceptionWrappers()
                .add(PlatformException::class.java) { command, exception ->
                    ServiceExecutionException(command, exception.code, exception)
                }
    }
}
