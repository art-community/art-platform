package ru.art.platform.agent.configuration;

import ru.art.core.factory.CollectionsFactory.*
import ru.art.platform.common.exception.*
import ru.art.reactive.service.configuration.ReactiveServiceModuleConfiguration.*
import ru.art.reactive.service.interception.*
import ru.art.reactive.service.wrapper.*
import ru.art.service.exception.*
import ru.art.service.interceptor.ServiceExecutionInterceptor.*
import ru.art.service.model.*
import ru.art.service.model.ServiceInterceptionResult.*

class ReactiveServiceConfiguration : ReactiveServiceModuleDefaultConfiguration() {
    override fun getRequestInterceptors(): MutableList<RequestInterceptor> {
        return linkedListOf(interceptRequest(object : ReactiveServiceLoggingInterception() {
            override fun intercept(request: ServiceRequest<*>): ServiceInterceptionResult {
                return nextInterceptor(request)
            }
        }), interceptRequest(ReactiveServiceValidationInterception()))
    }

    override fun getResponseInterceptors(): MutableList<ResponseInterceptor> {
        return linkedListOf(interceptResponse(object : ReactiveServiceLoggingInterception() {
            override fun intercept(request: ServiceRequest<*>): ServiceInterceptionResult {
                return nextInterceptor(request)
            }

            override fun intercept(request: ServiceRequest<*>, response: ServiceResponse<*>): ServiceInterceptionResult {
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
