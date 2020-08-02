package ru.art.platform.agent.constants

import ru.art.core.constants.StringConstants.SCHEME_DELIMITER
import ru.art.core.constants.StringConstants.SLASH
import ru.art.platform.agent.dsl.KanikoRepositoryCredentials

object ModuleImageConstants {
    const val MODULE_PATH = "/module"
}

object OpenShiftConstants {
    const val DEPLOYMENT_TRIGGER_VARIABLE_NAME = "TRIGGER"
    const val TRIGGER_LENGTH = 8
}

object GradleConstants {
    const val GRADLE_BUILD_CACHE = "--build-cache"
    const val GRADLE_STOP = "--stop"
    const val GRADLE_EXECUTABLE_6 = "gradle-6"
    const val GRADLE_EXECUTABLE_5 = "gradle-5"
    const val GRADLE_JDK_1_8_OPTION = "-Dorg.gradle.java.home=/usr/lib/jvm/java-8-openjdk-amd64"
    const val GRADLE_JDK_11_OPTION = "-Dorg.gradle.java.home=/usr/lib/jvm/java-11-openjdk-amd64"
    const val GRADLE_PROPERTY_FLAG = "-P"
    const val GRADLE_GROOVY_SCRIPT_FORMAT = "groovy"
    const val GRADLE_KTS_SCRIPT_FORMAT = "kts"
    const val GRADLE_INIT_SCRIPT_GROOVY_FILE = "init.gradle"
    const val GRADLE_INIT_SCRIPT_KTS_FILE = "init.gradle.kts"
    const val GRADLE_SETTINGS_FILE = "settings.gradle"
    const val GRADLE_PROJECT_DIR_PROPERTY_KEYWORD = ".projectDir"
    const val GRADLE_PROJECT_METHOD_KEYWORD = "project"
    const val GRADLE_INIT_SCRIPT_FLAG = "-I"
}

object TechnologyFiles {
    const val PACKAGE_JSON_FILE = "package.json"
    const val BUILD_GRADLE_FILE = "build.gradle"
    const val BUILD_GRADLE_KTS_FILE = "build.gradle.kts"
    const val YARN_LOCK_FILE = "yarn.lock"
    const val DOCKER_FILE = "Dockerfile"
    const val POM_XML_FILE = "pom.xml"
    const val JAVA_FILE_EXTENSION = ".java"
    const val KOTLIN_FILE_EXTENSION = ".kt"
    const val GROOVY_FILE_EXTENSION = ".groovy"
    const val JS_FILE_EXTENSION = ".js"
    const val TS_FILE_EXTENSION = ".ts"
}

object KanikoConstants {
    const val KANIKO_DOCKER_FILE_ARG = "--dockerfile"
    const val KANIKO_CONTEXT_ARG = "--context"
    const val KANIKO_DESTINATION_ARG = "--destination"
    const val KANIKO_TEMP_DOCKER_FILE = "--tempDockerfile"
    const val KANIKO_CACHE_ARG = "--cache"
    const val KANIKO_CACHE_REPO_ARG = "--cache-repo"
    const val KANIKO_SKIP_TLS_VERIFY_ARG = "--skip-tls-verify"
    const val KANIKO_INSECURE_ARG = "--insecure"
    const val KANIKO_IMAGE_ASSEMBLY_DIRECTORY_ARG = "--image-assembly-directory"
    const val KANIKO_SKIP_TLS_VERIFY_PULL_ARG = "--skip-tls-verify-pull"
    const val KANIKO_INSECURE_PULL_ARG = "--insecure-pull"
    const val KANIKO_DISABLE_PERMISSIONS_CHANGING_ARG = "--disable-permissions-changing"
    const val KANIKO_EXECUTABLE = "kaniko"
}

object DockerConstants {
    const val DOCKER_CONFIG_NAME = "config.json"
    const val DOCKER_CONFIG_ENVIRONMENT = "DOCKER_CONFIG"
    val DOCKER_CONFIG = { credentials: Set<KanikoRepositoryCredentials> ->
        val filtered = credentials.filter { credential -> !credential.userName.isNullOrBlank() && !credential.password.isNullOrBlank() }
        """ 
{
            	"auths": {
            ${filtered.joinToString { credential ->
            """ 
                "${credential.registryUrl.substringAfter(SCHEME_DELIMITER).substringBefore(SLASH)}": { "username": "${credential.userName}", "password": "${credential.password}" }
            """
        }} 
            }
}
        """.trimIndent()
    }
}

object GatlingConstants {
    const val GATLING_REPORTS_PATH = "reports/gatling"
}

object ServiceConstants {
    const val ALERT_MANAGER = "alertManager"
    const val ON_EVENT = "onEvent"
    const val ON_ALERT = "onAlert"
    const val GRAFANA = "grafana"
}

object CacheConstants {
    const val GRADLE_CACHE_JAR = "/opt/agent/tools/build-cache-node-9.0.jar"
    const val GRADLE_CACHE_DATA_DIR_ARGUMENT = "--data-dir"
    const val GRADLE_CACHE_PORT_ARGUMENT = "--port"
    const val GRADLE_CACHE_DIRECTORY = "cache/gradle"
    val GRADLE_CACHE_COMMAND = arrayOf("java", "-jar", GRADLE_CACHE_JAR)
}

object AlertManagerConstants {
    const val ALERT_MANAGER_STATUS = "status"
    const val ALERT_MANAGER_ALERT_NAME = "alertname"
    const val ALERT_MANAGER_ALERTS = "alerts"
    const val ALERT_MANAGER_LABELS = "labels"
    const val ALERT_MANAGER_ANNOTATIONS = "annotations"
    const val ALERT_MANAGER_STARTS_AT = "startsAt"
    const val ALERT_MANAGER_ENDS_AT = "endsAt"
    const val ALERT_MANAGER_FIRE_ICON = "🔥"
    const val ALERT_MANAGER_END_ICONS_COUNT = 11
    const val ALERT_MANAGER_INSTANCE = "instance"
    const val ALERT_MANAGER_MODULE = "module"
    const val ALERT_MANAGER_SYSTEM = "system"
    const val ALERT_MANAGER_NODE = "node"
    const val ALERT_MANAGER_APPLICATION = "application"
    const val ALERT_MANAGER_MRF = "mrf"
    const val ALERT_MANAGER_RF = "rf"
    const val ALERT_MANAGER_SEVERITY = "severity"
    const val ALERT_MANAGER_CRITICAL_ICON = "❗️"
    const val ALERT_MANAGER_WARNING_ICON = "⚠️"
    const val ALERT_MANAGER_DESCRIPTION = "description"
    const val ALERT_MANAGER_ALERT_FORMAT = "dd MMMM yyyy в HH:mm:ss"
    const val ALERT_MANAGER_RESOLVED_ICON = "☀️"
    const val ALERT_MANAGER_EMAIL_SUBJECT = "Уведомление "

    enum class AlertStatus(val status: String, val icon: String) {
        FIRING("firing", ALERT_MANAGER_FIRE_ICON),
        RESOLVED("resolved", ALERT_MANAGER_RESOLVED_ICON);
    }

    enum class AlertSeverity(val severity: String, val icon: String) {
        CRITICAL("critical", ALERT_MANAGER_CRITICAL_ICON),
        WARNING("warning", ALERT_MANAGER_WARNING_ICON);
    }

    fun parseAlertStatus(parsingStatus: String) = AlertStatus.values().single { status -> status.status == parsingStatus }

    fun parseAlertSeverity(parsingSeverity: String) = AlertSeverity.values().single { severity -> severity.severity == parsingSeverity }
}

object NotificationManagerConstants {
    const val NOTIFICATION_MANAGER_SUCCESS_ICON = "✅"
    const val NOTIFICATION_MANAGER_FAILED_ICON = "❌️"
    const val NOTIFICATION_MANAGER_STOP_ICON = "⚠️"
}


object GrafanaConstants {
    const val IMAGE_URL = "imageUrl"
    const val MESSAGE = "message"
    const val STATE = "state"
    const val TAGS = "tags"
}

object IntegramConstants {
    const val INTEGRAM_URL_ENVIRONMENT = "INTEGRAM_URL"
    const val TEXT = "text";
}

object DiscordConstants {
    const val CONTENT = "content";
    const val DISCORD_URL_ENVIRONMENT = "DISCORD_URL"
    const val DISCORD_WARNING_URL_ENVIRONMENT = "DISCORD_WARNING_URL"
    const val DISCORD_CRITICAL_URL_ENVIRONMENT = "DISCORD_CRITICAL_URL"
}

object SmtpConstants {
    const val SMTP_TO_ENVIRONMENT = "SMTP_TO"
    const val SMTP_FROM_ENVIRONMENT = "SMTP_FROM"
    const val SMTP_HOST_ENVIRONMENT = "SMTP_HOST"
    const val SMTP_PORT_ENVIRONMENT = "SMTP_PORT"
    const val MAIL_SMTP_HOST_PROPERTY = "mail.smtp.host"
    const val MAIL_SMTP_PORT_PROPERTY = "mail.smtp.port"
}

object ApplicationConstants {
    const val FILEBEAT_DATA_PATH = "/filebeat/data"
    const val FILEBEAT_CONFIG_PATH = "/etc/filebeat"
}

object AlertingNotificationConstants {
    const val ENABLE_TELEGRAM_ENVIRONMENT = "ENABLE_TELEGRAM"
    const val ENABLE_EMAIL_ENVIRONMENT = "ENABLE_EMAIL"
    const val ENABLE_DISCORD_ENVIRONMENT = "ENABLE_DISCORD"
    const val ENABLE_WHATSAPP_ENVIRONMENT = "ENABLE_WHATSAPP"
}

object IgnoringDirectories {
    const val NODE_MODULES_DIRECTORY = "node_modules"
    const val PY_CACHE_DIRECTORY = "__pycache__"

    val IGNORING_DIRECTORIES = arrayOf(NODE_MODULES_DIRECTORY, PY_CACHE_DIRECTORY)
}
