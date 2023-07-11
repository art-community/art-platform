package ru.art.platform.agent.configuration

import ru.art.config.extensions.rsocket.*
import ru.art.platform.common.constants.CommonConstants.*
import java.lang.System.*

class RsocketConfiguration : RsocketAgileConfiguration() {
    override fun getServerTcpPort(): Int = getenv()[RSOCKET_PORT_PROPERTY]?.toInt() ?: super.getServerTcpPort()
}