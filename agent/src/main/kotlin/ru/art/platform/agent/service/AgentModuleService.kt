package ru.art.platform.agent.service

import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers.elastic
import ru.art.platform.agent.manager.AgentOpenShiftModuleManager.deleteOnOpenShift
import ru.art.platform.agent.manager.AgentOpenShiftModuleManager.installOnOpenShift
import ru.art.platform.agent.manager.AgentOpenShiftModuleManager.restartOnOpenShift
import ru.art.platform.agent.manager.AgentOpenShiftModuleManager.stopOnOpenShift
import ru.art.platform.agent.manager.AgentOpenShiftModuleManager.updateOnOpenShift
import ru.art.platform.agent.service.NotificationManagerService.sendDeleteErrorNotification
import ru.art.platform.agent.service.NotificationManagerService.sendDeleteSuccessNotification
import ru.art.platform.agent.service.NotificationManagerService.sendInstallErrorNotification
import ru.art.platform.agent.service.NotificationManagerService.sendInstallSuccessNotification
import ru.art.platform.agent.service.NotificationManagerService.sendRestartErrorNotification
import ru.art.platform.agent.service.NotificationManagerService.sendRestartSuccessNotification
import ru.art.platform.agent.service.NotificationManagerService.sendStopErrorNotification
import ru.art.platform.agent.service.NotificationManagerService.sendStopSuccessNotification
import ru.art.platform.agent.service.NotificationManagerService.sendUpdateErrorNotification
import ru.art.platform.agent.service.NotificationManagerService.sendUpdateSuccessNotification
import ru.art.platform.api.model.module.Module
import ru.art.platform.api.model.request.*
import ru.art.platform.common.constants.Resources.OPEN_SHIFT_RESOURCE
import ru.art.platform.common.emitter.reactiveEmitter

object AgentModuleService {
    fun installModule(request: AgentModuleInstallRequest): Flux<Module> = reactiveEmitter<Module, Module.ModuleBuilder>(request.module)
            .from { build() }
            .to { toBuilder() }
            .defer {
                when (current().resourceId.type) {
                    OPEN_SHIFT_RESOURCE -> installOnOpenShift(request)
                }
                complete()
            }
            .doOnComplete { sendInstallSuccessNotification(request) }
            .doOnError { sendInstallErrorNotification(request) }
            .subscribeOn(elastic(), false)

    fun updateModule(request: AgentModuleUpdateRequest): Flux<Module> = reactiveEmitter<Module, Module.ModuleBuilder>(request.newModule)
            .from { build() }
            .to { toBuilder() }
            .defer {
                when (request.newModule.resourceId.type) {
                    OPEN_SHIFT_RESOURCE -> updateOnOpenShift(request)
                }
                complete()
            }
            .doOnComplete { sendUpdateSuccessNotification(request) }
            .doOnError { sendUpdateErrorNotification(request) }
            .subscribeOn(elastic(), false)

    fun stopModule(request: AgentModuleStopRequest): Flux<Module> = reactiveEmitter<Module, Module.ModuleBuilder>(request.module)
            .from { build() }
            .to { toBuilder() }
            .defer {
                when (current().resourceId.type) {
                    OPEN_SHIFT_RESOURCE -> stopOnOpenShift(request)
                }

                complete()
            }
            .doOnComplete { sendStopSuccessNotification(request) }
            .doOnError { sendStopErrorNotification(request) }
            .subscribeOn(elastic(), false)

    fun restartModule(request: AgentModuleRestartRequest): Flux<Module> = reactiveEmitter<Module, Module.ModuleBuilder>(request.module)
            .from { build() }
            .to { toBuilder() }
            .defer {
                when (current().resourceId.type) {
                    OPEN_SHIFT_RESOURCE -> restartOnOpenShift(request)
                }
                complete()
            }
            .doOnComplete { sendRestartSuccessNotification(request) }
            .doOnError { sendRestartErrorNotification(request) }
            .subscribeOn(elastic(), false)

    fun deleteModule(request: AgentModuleDeleteRequest): Flux<Module> = reactiveEmitter<Module, Module.ModuleBuilder>(request.module)
            .from { build() }
            .to { toBuilder() }
            .defer {
                when (current().resourceId.type) {
                    OPEN_SHIFT_RESOURCE -> deleteOnOpenShift(request)
                }
                complete()
            }
            .doOnComplete { sendDeleteSuccessNotification(request) }
            .doOnError { sendDeleteErrorNotification(request) }
            .subscribeOn(elastic(), false)
}
