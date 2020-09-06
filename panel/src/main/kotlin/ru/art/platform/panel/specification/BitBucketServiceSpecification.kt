package ru.art.platform.panel.specification

import ru.art.entity.*
import ru.art.http.constants.MimeToContentTypeMapper.*
import ru.art.http.server.function.HttpServiceFunction.*
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.platform.panel.constants.ServiceConstants.BIT_BUCKET
import ru.art.platform.panel.constants.ServiceConstants.ON_EVENT
import ru.art.platform.panel.service.*
import ru.art.service.constants.RequestValidationPolicy.*

fun registerBitBucketService() =
        httpPost("/$PLATFORM_CAMEL_CASE/$API_CAMEL_CASE/$BIT_BUCKET/$ON_EVENT")
                .fromBody()
                .consumesMimeType(applicationJsonUtf8())
                .validationPolicy(NOT_NULL)
                .requestMapper { value -> value as Entity? }
                .consume(BitBucketService::onEvent)
