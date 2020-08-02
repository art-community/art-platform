package ru.art.platform.agent.service

import ru.art.entity.Entity
import ru.art.platform.agent.factory.GrafanaNotificationsFactory.basicGrafanaNotification
import ru.art.platform.agent.service.IntegramService.sendTelegramMessage

object GrafanaService {
    fun onAlert(data: Entity) = sendTelegramMessage(basicGrafanaNotification(data))
}
