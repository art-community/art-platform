package ru.art.platform.agent.service

import ru.art.core.constants.DateConstants.*
import ru.art.core.constants.StringConstants.*
import ru.art.core.extension.ExceptionExtensions.nullIfException
import ru.art.entity.Entity
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_ALERTS
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_ALERT_FORMAT
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_ALERT_NAME
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_ANNOTATIONS
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_APPLICATION
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_DESCRIPTION
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_ENDS_AT
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_END_ICONS_COUNT
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_INSTANCE
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_LABELS
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_MODULE
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_MRF
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_NODE
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_RF
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_SEVERITY
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_STARTS_AT
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_STATUS
import ru.art.platform.agent.constants.AlertManagerConstants.ALERT_MANAGER_SYSTEM
import ru.art.platform.agent.constants.AlertManagerConstants.AlertSeverity
import ru.art.platform.agent.constants.AlertManagerConstants.AlertSeverity.CRITICAL
import ru.art.platform.agent.constants.AlertManagerConstants.AlertSeverity.WARNING
import ru.art.platform.agent.constants.AlertManagerConstants.AlertStatus
import ru.art.platform.agent.constants.AlertManagerConstants.AlertStatus.RESOLVED
import ru.art.platform.agent.constants.AlertManagerConstants.parseAlertSeverity
import ru.art.platform.agent.constants.AlertManagerConstants.parseAlertStatus
import ru.art.platform.agent.constants.AlertingNotificationConstants.ENABLE_DISCORD_ENVIRONMENT
import ru.art.platform.agent.constants.AlertingNotificationConstants.ENABLE_EMAIL_ENVIRONMENT
import ru.art.platform.agent.constants.AlertingNotificationConstants.ENABLE_TELEGRAM_ENVIRONMENT
import ru.art.platform.agent.constants.DiscordConstants.DISCORD_CRITICAL_URL_ENVIRONMENT
import ru.art.platform.agent.constants.DiscordConstants.DISCORD_WARNING_URL_ENVIRONMENT
import ru.art.platform.agent.service.DiscordService.sendDiscordMessage
import ru.art.platform.agent.service.EmailService.sendEmail
import ru.art.platform.agent.service.IntegramService.sendTelegramMessage
import ru.art.platform.common.constants.CommonConstants.REGEX_ANY
import ru.art.platform.common.constants.CommonConstants.RUSSIAN_LOCALE
import ru.art.platform.common.constants.ErrorCodes.PLATFORM_ERROR
import ru.art.task.deferred.executor.SchedulerModuleActions.asynchronous
import java.lang.System.getenv
import java.text.SimpleDateFormat
import java.time.ZoneId.systemDefault
import java.time.ZonedDateTime.ofInstant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.*
import java.util.*
import java.util.stream.IntStream.range


object AlertManagerService {
    fun onEvent(requestEntity: Entity?) {
        requestEntity?.let { request ->
            parseAlerts(request.getEntityList(ALERT_MANAGER_ALERTS))
                    .map(::createAlertMessage)
                    .parallelStream()
                    .forEach { message ->
                        getenv(ENABLE_TELEGRAM_ENVIRONMENT)?.apply {
                            asynchronous { sendTelegramMessage(message.write()) }
                        }
                        getenv(ENABLE_EMAIL_ENVIRONMENT)?.apply {
                            asynchronous {
                                sendEmail(message.subject(), message.write())
                            }
                        }
                        getenv(ENABLE_DISCORD_ENVIRONMENT)?.apply {
                            asynchronous {
                                sendDiscordMessage(message.write(), url = when (message.severity()) {
                                    CRITICAL -> getenv(DISCORD_CRITICAL_URL_ENVIRONMENT)
                                    WARNING -> getenv(DISCORD_WARNING_URL_ENVIRONMENT)
                                })
                            }
                        }
                    }
        }
    }
}

private fun parseAlerts(alerts: List<Entity>): List<Alert> = alerts.map { alert ->
    Alert(
            labels = alert.getStringParameters(ALERT_MANAGER_LABELS),
            annotations = alert.getStringParameters(ALERT_MANAGER_ANNOTATIONS),
            startsAt = alert.getString(ALERT_MANAGER_STARTS_AT),
            endsAt = alert.getString(ALERT_MANAGER_ENDS_AT),
            status = parseAlertStatus(alert.getString(ALERT_MANAGER_STATUS))
    )
}

private fun createAlertMessage(alert: Alert): AlertMessage {
    val startDateTimeWithoutTz = alert.startsAt.split(REGEX_ANY)[0]
    val startDateTime = YYYY_MM_DD_T_HH_MM_SS_24H_DASH_FORMAT.get().parse(startDateTimeWithoutTz.substring(0, startDateTimeWithoutTz.lastIndexOf(DOT)))
    val pattern = ofPattern(ALERT_MANAGER_ALERT_FORMAT, RUSSIAN_LOCALE)
    val startDateTimeAsString = pattern.format(startDateTime.toInstant().plusSeconds(startDateTime.toInstant().atZone(systemDefault()).offset.totalSeconds.toLong()))
    val endDateTime = nullIfException {
        val endDateTimeWithoutTz = alert.endsAt.split(REGEX_ANY)[0]
        YYYY_MM_DD_T_HH_MM_SS_24H_DASH_FORMAT.get().parse(endDateTimeWithoutTz.substring(0, endDateTimeWithoutTz.lastIndexOf(DOT)))
    }
    val endDateTimeAsString = endDateTime?.let { time ->
        pattern.format(time.toInstant().plusSeconds(time.toInstant().atZone(systemDefault()).offset.totalSeconds.toLong()))
    }

    return when {
        alert.labels.containsKey(ALERT_MANAGER_MODULE) -> ModuleAlertMessage(
                labels = alert.labels,
                status = alert.status,
                severity = parseAlertSeverity(alert.labels[ALERT_MANAGER_SEVERITY] ?: error(PLATFORM_ERROR)),
                description = alert.annotations[ALERT_MANAGER_DESCRIPTION] ?: error(PLATFORM_ERROR),
                startTime = startDateTimeAsString,
                endTime = endDateTimeAsString,
                module = alert.labels[ALERT_MANAGER_INSTANCE] ?: error(PLATFORM_ERROR),
                name = alert.labels[ALERT_MANAGER_ALERT_NAME] ?: error(PLATFORM_ERROR)
        )

        alert.labels.containsKey(ALERT_MANAGER_SYSTEM) -> ExternalSystemAlertMessage(
                labels = alert.labels,
                status = alert.status,
                severity = parseAlertSeverity(alert.labels[ALERT_MANAGER_SEVERITY] ?: error(PLATFORM_ERROR)),
                description = alert.annotations[ALERT_MANAGER_DESCRIPTION] ?: error(PLATFORM_ERROR),
                startTime = startDateTimeAsString,
                endTime = endDateTimeAsString,
                system = alert.labels[ALERT_MANAGER_SYSTEM] ?: error(PLATFORM_ERROR),
                instance = alert.labels[ALERT_MANAGER_INSTANCE] ?: error(PLATFORM_ERROR),
                mrf = alert.labels[ALERT_MANAGER_MRF] ?: error(PLATFORM_ERROR),
                rf = alert.labels[ALERT_MANAGER_RF] ?: error(PLATFORM_ERROR),
                name = alert.labels[ALERT_MANAGER_ALERT_NAME] ?: error(PLATFORM_ERROR)
        )

        alert.labels.containsKey(ALERT_MANAGER_NODE) -> NodeAlertMessage(
                labels = alert.labels,
                status = alert.status,
                severity = parseAlertSeverity(alert.labels[ALERT_MANAGER_SEVERITY] ?: error(PLATFORM_ERROR)),
                description = alert.annotations[ALERT_MANAGER_DESCRIPTION] ?: error(PLATFORM_ERROR),
                startTime = startDateTimeAsString,
                endTime = endDateTimeAsString,
                instance = alert.labels[ALERT_MANAGER_INSTANCE] ?: error(PLATFORM_ERROR),
                name = alert.labels[ALERT_MANAGER_ALERT_NAME] ?: error(PLATFORM_ERROR)
        )

        alert.labels.containsKey(ALERT_MANAGER_APPLICATION) -> ApplicationAlertMessage(
                labels = alert.labels,
                status = alert.status,
                severity = parseAlertSeverity(alert.labels[ALERT_MANAGER_SEVERITY] ?: error(PLATFORM_ERROR)),
                description = alert.annotations[ALERT_MANAGER_DESCRIPTION] ?: error(PLATFORM_ERROR),
                startTime = startDateTimeAsString,
                endTime = endDateTimeAsString,
                instance = alert.labels[ALERT_MANAGER_INSTANCE] ?: error(PLATFORM_ERROR),
                application = alert.labels[ALERT_MANAGER_APPLICATION] ?: error(PLATFORM_ERROR),
                name = alert.labels[ALERT_MANAGER_ALERT_NAME] ?: error(PLATFORM_ERROR)
        )

        else -> object : AlertMessage {
            override fun severity() = WARNING
            override fun write() = "Тревога:  ${alert.labels[ALERT_MANAGER_ALERT_NAME] ?: error(PLATFORM_ERROR)}"
            override fun subject(): String = alert.labels[ALERT_MANAGER_ALERT_NAME] ?: error(PLATFORM_ERROR)
        }
    }
}

private data class Alert(
        val labels: Map<String, String>,
        val annotations: Map<String, String>,
        val startsAt: String,
        val endsAt: String,
        val status: AlertStatus
)

private interface AlertMessage {
    fun severity(): AlertSeverity
    fun write(): String
    fun subject(): String
}

private class ModuleAlertMessage(val name: String,
                                 val module: String,
                                 val labels: Map<String, String>,
                                 val severity: AlertSeverity,
                                 val status: AlertStatus,
                                 val description: String,
                                 val startTime: String,
                                 val endTime: String?) : AlertMessage {
    override fun severity() = severity

    override fun write(): String {
        val resolved = status == RESOLVED
        return """
${status.icon} Проблема с модулем $module ${if (resolved) "устранена" else EMPTY_STRING}

Критичность: ${severity.icon} 

$description

${labels.map { (key, value) -> "$key: $value" }.joinToString(NEW_LINE)}
Проблема возникла $startTime
${if (resolved) "Проблема устранена $endTime" else EMPTY_STRING}
${range(1, ALERT_MANAGER_END_ICONS_COUNT).boxed().map { status.icon }.reduce(status.icon, String::plus)}
            """.trimIndent()
    }

    override fun subject(): String = name
}

private class ApplicationAlertMessage(val name: String,
                                      val instance: String,
                                      val application: String,
                                      val severity: AlertSeverity,
                                      val status: AlertStatus,
                                      val description: String,
                                      val labels: Map<String, String>,
                                      val startTime: String,
                                      val endTime: String?) : AlertMessage {
    override fun severity() = severity

    override fun write(): String {
        val resolved = status == RESOLVED
        return """
${status.icon} Проблема с приложением $application ${if (resolved) "устранена" else EMPTY_STRING}

Критичность: ${severity.icon} 
Адрес: $instance
$description

${labels.map { (key, value) -> "$key: $value" }.joinToString(NEW_LINE)}
Проблема возникла $startTime
${if (resolved) "Проблема устранена $endTime" else EMPTY_STRING}
${range(1, ALERT_MANAGER_END_ICONS_COUNT).boxed().map { status.icon }.reduce(status.icon, String::plus)}
            """.trimIndent()
    }

    override fun subject(): String = name
}


private class NodeAlertMessage(val name: String,
                               val instance: String,
                               val severity: AlertSeverity,
                               val status: AlertStatus,
                               val description: String,
                               val startTime: String,
                               val labels: Map<String, String>,
                               val endTime: String?) : AlertMessage {
    override fun severity() = severity

    override fun write(): String {
        val resolved = status == RESOLVED
        return """
${status.icon} Проблема с сервером  $instance ${if (resolved) "устранена" else EMPTY_STRING}

Критичность: ${severity.icon} 
Адрес: $instance
$description

${labels.map { (key, value) -> "$key: $value" }.joinToString(NEW_LINE)}
Проблема возникла $startTime
${if (resolved) "Проблема устранена $endTime" else EMPTY_STRING}
${range(1, ALERT_MANAGER_END_ICONS_COUNT).boxed().map { status.icon }.reduce(status.icon, String::plus)}
            """.trimIndent()
    }

    override fun subject(): String = name
}

private class ExternalSystemAlertMessage(val name: String,
                                         val system: String,
                                         val instance: String,
                                         val mrf: String?,
                                         val rf: String?,
                                         val severity: AlertSeverity,
                                         val status: AlertStatus,
                                         val description: String,
                                         val labels: Map<String, String>,
                                         val startTime: String,
                                         val endTime: String?) : AlertMessage {
    override fun severity() = severity

    override fun write(): String {
        val resolved = status == RESOLVED
        return """
${status.icon} Проблема с системой $system ${if (resolved) "устранена" else EMPTY_STRING}

Критичность: ${severity.icon} 
Адрес: $instance 
${if (!mrf.isNullOrBlank()) "МРФ: $mrf\n" else EMPTY_STRING}${if (!rf.isNullOrBlank()) "РФ: $rf" else EMPTY_STRING}
$description

${labels.map { (key, value) -> "$key: $value" }.joinToString(NEW_LINE)}
Проблема возникла $startTime
${if (resolved) "Проблема устранена $endTime" else EMPTY_STRING}
${range(1, ALERT_MANAGER_END_ICONS_COUNT).boxed().map { status.icon }.reduce(status.icon, String::plus)}
            """.trimIndent()
    }

    override fun subject(): String = name
}
