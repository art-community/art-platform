package ru.art.platform.agent.specification

import ru.art.entity.Entity
import ru.art.http.constants.MimeToContentTypeMapper.applicationJsonUtf8
import ru.art.http.server.function.HttpServiceFunction.httpPost
import ru.art.platform.agent.constants.ServiceConstants.GRAFANA
import ru.art.platform.agent.constants.ServiceConstants.ON_ALERT
import ru.art.platform.agent.service.GrafanaService
import ru.art.platform.common.constants.PlatformKeywords.AGENT_CAMEL_CASE
import ru.art.platform.common.constants.PlatformKeywords.API_CAMEL_CASE

fun registerGrafanaService() = httpPost("/$AGENT_CAMEL_CASE/$API_CAMEL_CASE/$GRAFANA/$ON_ALERT")
        .ignoreRequestContentType()
        .fromBody()
        .consumesMimeType(applicationJsonUtf8())
        .requestMapper { it as Entity }
        .consume(GrafanaService::onAlert)