package ru.art.platform.configuration

import ru.art.config.extensions.http.*
import ru.art.http.server.*
import ru.art.platform.constants.WebConstants.EXTERNAL_RSOCKET_HOST_SYSTEM_ENV
import ru.art.platform.constants.WebConstants.EXTERNAL_RSOCKET_PORT_SYSTEM_ENV
import ru.art.platform.constants.WebConstants.EXTERNAL_RSOCKET_PROTOCOL_SYSTEM_ENV
import ru.art.platform.constants.WebConstants.RSOCKET_HOST_VARIABLE
import ru.art.platform.constants.WebConstants.RSOCKET_PORT_VARIABLE
import ru.art.platform.constants.WebConstants.RSOCKET_PROTOCOL_VARIABLE
import java.lang.System.*

class HttpServerConfiguration : HttpServerAgileConfiguration() {
    override fun getResourceConfiguration(): HttpServerModuleConfiguration.HttpResourceConfiguration {
        val protocol = getenv(EXTERNAL_RSOCKET_PROTOCOL_SYSTEM_ENV)
        val host = getenv(EXTERNAL_RSOCKET_HOST_SYSTEM_ENV)
        val port = getenv(EXTERNAL_RSOCKET_PORT_SYSTEM_ENV)
        if (host.isNullOrBlank() || port.isNullOrBlank()) {
            return super.getResourceConfiguration()
        }
        return super.getResourceConfiguration().toBuilder()
                .templateResourceVariable(RSOCKET_PROTOCOL_VARIABLE, protocol)
                .templateResourceVariable(RSOCKET_HOST_VARIABLE, host)
                .templateResourceVariable(RSOCKET_PORT_VARIABLE, port)
                .build()
    }
}
