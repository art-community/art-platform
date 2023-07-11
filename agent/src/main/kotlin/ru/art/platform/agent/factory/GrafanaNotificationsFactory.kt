package ru.art.platform.agent.factory

import ru.art.core.constants.StringConstants.NEW_LINE
import ru.art.entity.Entity
import ru.art.platform.agent.constants.GrafanaConstants.IMAGE_URL
import ru.art.platform.agent.constants.GrafanaConstants.MESSAGE
import ru.art.platform.agent.constants.GrafanaConstants.STATE
import ru.art.platform.agent.constants.GrafanaConstants.TAGS

object GrafanaNotificationsFactory {
    fun basicGrafanaNotification(alertData: Entity): String {
        val imageUrl = alertData.getString(IMAGE_URL)
        val message = alertData.getString(MESSAGE)
        val state = alertData.getString(STATE)
        val tags = alertData.getEntity(TAGS)
        return """
❗️❗️❗️
            Тревога: $message
            Состояние: $state
            ${tags.fields.entries.joinToString(NEW_LINE) { entry -> "${entry.key}: ${entry.value}" }}
            $imageUrl
        """.trimIndent()
    }
}