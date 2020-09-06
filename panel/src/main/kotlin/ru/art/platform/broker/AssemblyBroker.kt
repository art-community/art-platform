package ru.art.platform.broker

import ru.art.platform.api.mapping.assembly.AssemblyMapper.*
import ru.art.platform.api.model.assembly.*
import ru.art.platform.common.broker.*
import ru.art.platform.common.constants.EventTypes.*

private val assemblyBroker = ReactiveBroker()

fun assemblyAdded(assembly: Assembly) = assemblyBroker.emit(PlatformEvent(ADD_EVENT, fromAssembly.map(assembly)))

fun assemblyUpdated(assembly: Assembly) = assemblyBroker.emit(PlatformEvent(UPDATE_EVENT, fromAssembly.map(assembly)))

fun assemblyDeleted(assembly: Assembly) = assemblyBroker.emit(PlatformEvent(DELETE_EVENT, fromAssembly.map(assembly)))

fun Assembly.updated(): Assembly {
    assemblyUpdated(this)
    return this
}

fun Assembly.added(): Assembly {
    assemblyAdded(this)
    return this
}

fun Assembly.deleted(): Assembly {
    assemblyDeleted(this)
    return this
}

fun assemblyConsumer() = assemblyBroker.consumer()