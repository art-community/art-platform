package ru.art.platform.agent.service

import ru.art.core.constants.StringConstants.*
import ru.art.entity.Entity.entityBuilder
import ru.art.platform.agent.communication.HttpProxyCommunicator.sendHttpRequest
import ru.art.platform.agent.constants.DiscordConstants.CONTENT
import ru.art.platform.agent.constants.DiscordConstants.DISCORD_URL_ENVIRONMENT
import ru.art.platform.agent.model.HttpProxyConfiguration
import ru.art.platform.common.constants.CommonConstants.HTTP_PROXY_ENVIRONMENT
import java.lang.System.getenv

object DiscordService {
    fun sendDiscordMessage(message: String, url: String? = getenv(DISCORD_URL_ENVIRONMENT)) {
        url ?: return
        val proxy = getenv(HTTP_PROXY_ENVIRONMENT)?.substringAfter(SCHEME_DELIMITER) ?: return

        val proxyConfiguration = HttpProxyConfiguration(
                userName = proxy.substringBefore(COLON),
                password = proxy.substringAfter(COLON).substringBefore(AT_SIGN),
                host = proxy.substringAfter(AT_SIGN).substringBefore(COLON),
                port = proxy.substringAfterLast(COLON).substringBefore(SLASH).toInt()
        )

        val request = entityBuilder().stringField(CONTENT, message).build()
        sendHttpRequest(proxyConfiguration, url, request)
    }
}
