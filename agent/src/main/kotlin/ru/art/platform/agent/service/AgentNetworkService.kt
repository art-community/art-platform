package ru.art.platform.agent.service

import ru.art.platform.api.model.network.*
import ru.art.platform.common.constants.Resources.*
import java.net.*

object AgentNetworkService {
    fun checkNetworkAccess(request: NetworkAccessRequest): Boolean {
        when (request.resourceId.type) {
            OPEN_SHIFT_RESOURCE -> return try {
                Socket().connect(InetSocketAddress(request.hostName, request.port), request.timeout)
                true
            } catch (ignore: Throwable) {
                false
            }
        }
        return false
    }
}