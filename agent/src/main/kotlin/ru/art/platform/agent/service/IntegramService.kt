package ru.art.platform.agent.service

import ru.art.core.constants.StringConstants.*
import ru.art.entity.Entity.entityBuilder
import ru.art.platform.agent.communication.HttpProxyCommunicator.sendHttpRequest
import ru.art.platform.agent.constants.IntegramConstants.INTEGRAM_URL_ENVIRONMENT
import ru.art.platform.agent.constants.IntegramConstants.TEXT
import ru.art.platform.agent.model.HttpProxyConfiguration
import ru.art.platform.agent.model.toConfiguration
import ru.art.platform.api.model.resource.ProxyResource
import ru.art.platform.common.constants.CommonConstants.HTTP_PROXY_ENVIRONMENT
import java.lang.System.getenv

object IntegramService {
    fun sendTelegramMessage(message: String, integramUrl: String = getenv(INTEGRAM_URL_ENVIRONMENT)) {
        val proxy = getenv(HTTP_PROXY_ENVIRONMENT)?.substringAfter(SCHEME_DELIMITER) ?: return

        val proxyConfiguration = HttpProxyConfiguration(
                userName = proxy.substringBefore(COLON),
                password = proxy.substringAfter(COLON).substringBefore(AT_SIGN),
                host = proxy.substringAfter(AT_SIGN).substringBefore(COLON),
                port = proxy.substringAfterLast(COLON).substringBefore(SLASH).toInt()
        )

        val request = entityBuilder().stringField(TEXT, message).build()
        sendHttpRequest(proxyConfiguration, integramUrl, request)
    }

    fun sendTelegramMessage(message: String, integramUrl: String, proxy: ProxyResource?) {
        proxy ?: return
        val request = entityBuilder().stringField(TEXT, message).build()
        sendHttpRequest(proxy.toConfiguration(), integramUrl, request)
    }
}
