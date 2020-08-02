package ru.art.platform.agent.specification

import ru.art.entity.Entity
import ru.art.http.constants.MimeToContentTypeMapper.applicationJsonUtf8
import ru.art.http.server.function.HttpServiceFunction.httpPost
import ru.art.platform.agent.constants.ServiceConstants.ALERT_MANAGER
import ru.art.platform.agent.constants.ServiceConstants.ON_EVENT
import ru.art.platform.agent.service.AlertManagerService
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.service.constants.RequestValidationPolicy.NOT_NULL

fun registerAlertManagerService() =
        httpPost("/$AGENT_CAMEL_CASE/$API_CAMEL_CASE/$ALERT_MANAGER/$ON_EVENT")
                .fromBody()
                .consumesMimeType(applicationJsonUtf8())
                .validationPolicy(NOT_NULL)
                .requestMapper { value -> value as Entity? }
                .consume(AlertManagerService::onEvent)