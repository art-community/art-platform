package ru.art.platform.agent.model

import ru.art.platform.api.model.resource.ProxyResource

data class HttpProxyConfiguration(val host: String, val port: Int, val userName: String?, val password: String?)

fun ProxyResource.toConfiguration() = HttpProxyConfiguration(
        host = host,
        port = port,
        userName = userName,
        password = password
)
