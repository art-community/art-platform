package ru.art.platform.broker

import ru.art.platform.api.mapping.module.ModuleMapper.*
import ru.art.platform.api.model.module.*
import ru.art.platform.common.broker.*
import ru.art.platform.common.constants.EventTypes.*

private val moduleBroker = ReactiveBroker()

fun moduleAdded(module: Module) = moduleBroker.emit(PlatformEvent(ADD_EVENT, fromModule.map(module)))

fun moduleUpdated(module: Module) = moduleBroker.emit(PlatformEvent(UPDATE_EVENT, fromModule.map(module)))

fun moduleDeleted(module: Module) = moduleBroker.emit(PlatformEvent(DELETE_EVENT, fromModule.map(module)))

fun Module.updated(): Module {
    moduleUpdated(this)
    return this
}

fun Module.added(): Module {
    moduleAdded(this)
    return this
}

fun Module.deleted(): Module {
    moduleDeleted(this)
    return this
}

fun moduleConsumer() = moduleBroker.consumer()