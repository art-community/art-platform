package ru.art.platform.panel.configuration

import io.netty.handler.ssl.SslContextBuilder.forServer
import io.rsocket.plugins.RSocketInterceptor
import reactor.netty.http.server.HttpServer
import ru.art.config.extensions.ConfigExtensions.configBoolean
import ru.art.config.extensions.ConfigExtensions.configString
import ru.art.config.extensions.rsocket.RsocketAgileConfiguration
import ru.art.core.factory.CollectionsFactory.linkedListOf
import ru.art.entity.Value
import ru.art.entity.Value.asPrimitive
import ru.art.entity.Value.isPrimitive
import ru.art.platform.common.constants.ErrorCodes.UNAUTHORIZED
import ru.art.platform.common.constants.PlatformKeywords.PLATFORM_CAMEL_CASE
import ru.art.platform.panel.constants.ConfigKeys.SSL_CERT_PATH_KEY
import ru.art.platform.panel.constants.ConfigKeys.SSL_ENABLED_KEY
import ru.art.platform.panel.constants.ConfigKeys.SSL_KEY_PATH_KEY
import ru.art.platform.panel.constants.ConfigKeys.SSL_STORE_PASSWORD
import ru.art.platform.panel.constants.ServiceConstants.AUTHENTICATE
import ru.art.platform.panel.constants.ServiceConstants.AUTHORIZE
import ru.art.platform.panel.constants.ServiceConstants.GET_USER_EMAILS
import ru.art.platform.panel.constants.ServiceConstants.GET_USER_NAMES
import ru.art.platform.panel.constants.ServiceConstants.REGISTER_USER
import ru.art.platform.panel.repository.UserRepository.getUserByToken
import ru.art.platform.panel.service.TokenService.checkToken
import ru.art.platform.panel.state.SessionState.localUser
import ru.art.rsocket.constants.RsocketModuleConstants.RsocketInterceptedResultAction.PROCESS
import ru.art.rsocket.constants.RsocketModuleConstants.RsocketInterceptedResultAction.RETURN
import ru.art.rsocket.factory.RsocketFunctionPredicateFactory.byRsocketFunction
import ru.art.rsocket.interceptor.RsocketValueInterceptor.rsocketMetaDataInterceptor
import ru.art.rsocket.model.RsocketValueInterceptionResult.intercepted
import ru.art.service.factory.ServiceResponseFactory.errorResponse
import ru.art.service.mapping.ServiceResponseMapping.fromServiceResponse
import java.io.File
import java.util.function.Function

class RsocketConfiguration : RsocketAgileConfiguration() {
    override fun getWebSocketServerConfigurator(): Function<out HttpServer, out HttpServer> = Function { server ->
        when {
            configBoolean(PLATFORM_CAMEL_CASE, SSL_ENABLED_KEY, false) -> server.secure { ssl ->
                ssl.sslContext(forServer(File(configString(SSL_CERT_PATH_KEY)), File(configString(SSL_KEY_PATH_KEY)), configString(SSL_STORE_PASSWORD)))
            }
            else -> server
        }
    }

    override fun getServerInterceptors(): MutableList<RSocketInterceptor> {
        val predicate =
                byRsocketFunction(AUTHENTICATE)
                        .or(byRsocketFunction(AUTHORIZE))
                        .or(byRsocketFunction(GET_USER_NAMES))
                        .or(byRsocketFunction(GET_USER_EMAILS))
                        .or(byRsocketFunction(REGISTER_USER))
        val interceptor = rsocketMetaDataInterceptor()
                .predicate(predicate.negate())
                .onFireAndForget { value, _ ->
                    if (!authenticate(value.value)) {
                        return@onFireAndForget RETURN;
                    }
                    return@onFireAndForget PROCESS;
                }
                .onRequestResponse { value, _ ->
                    if (!authenticate(value.value)) {
                        return@onRequestResponse intercepted(createErrorEntity(), RETURN)
                    }
                    return@onRequestResponse intercepted(PROCESS)
                }
                .onRequestStream { value, _ ->
                    if (!authenticate(value.value)) {
                        return@onRequestStream intercepted(createErrorEntity(), RETURN)
                    }
                    return@onRequestStream intercepted(PROCESS)
                }
                .onRequestChannel { value, _ ->
                    if (!authenticate(value.value)) {
                        return@onRequestChannel intercepted(createErrorEntity(), RETURN)
                    }
                    return@onRequestChannel intercepted(PROCESS)
                }
                .onMetadataPush { value, _ ->
                    if (!authenticate(value.value)) {
                        return@onMetadataPush RETURN
                    }
                    return@onMetadataPush PROCESS
                }
        return linkedListOf<RSocketInterceptor>(super.getServerInterceptors()).apply { add(interceptor) }
    }
}

private fun authenticate(value: Value?) = value
        ?.takeIf { token -> isPrimitive(token) }
        ?.let { token -> checkToken(asPrimitive(token).string).apply { localUser.set(getUserByToken(asPrimitive(token).string)) } }
        ?: false

private fun createErrorEntity() = fromServiceResponse<Any>().map(errorResponse(UNAUTHORIZED, UNAUTHORIZED))
