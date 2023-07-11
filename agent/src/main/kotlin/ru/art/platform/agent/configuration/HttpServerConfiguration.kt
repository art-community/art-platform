package ru.art.platform.agent.configuration

import ru.art.config.extensions.http.*
import ru.art.platform.common.constants.CommonConstants.*
import java.lang.System.*

class HttpServerConfiguration : HttpServerAgileConfiguration() {
    override fun getPort(): Int = getenv()[HTTP_PORT_PROPERTY]?.toInt() ?: super.getPort()
}