package ru.art.platform.panel.specification

import ru.art.http.server.function.HttpServiceFunction.httpPost
import ru.art.platform.common.constants.PlatformKeywords.API_CAMEL_CASE
import ru.art.platform.common.constants.PlatformKeywords.PLATFORM_CAMEL_CASE
import ru.art.platform.panel.constants.ServiceConstants.BACKUP
import ru.art.platform.panel.service.BackupService

fun registerBackupService() =
        httpPost("/$PLATFORM_CAMEL_CASE/$API_CAMEL_CASE/$BACKUP").produce(BackupService::processBackup)
