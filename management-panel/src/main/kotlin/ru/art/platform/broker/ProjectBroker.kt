package ru.art.platform.broker

import reactor.core.publisher.*
import ru.art.platform.api.mapping.project.ProjectMapper.*
import ru.art.platform.api.model.project.*
import ru.art.platform.common.broker.*
import ru.art.platform.common.constants.EventTypes.*

private val projectBroker = ReactiveBroker()

fun projectAdded(project: Project) = projectBroker.emit(PlatformEvent(ADD_EVENT, fromProject.map(project)))

fun projectUpdated(project: Project) = projectBroker.emit(PlatformEvent(UPDATE_EVENT, fromProject.map(project)))

fun projectDeleted(project: Project) = projectBroker.emit(PlatformEvent(DELETE_EVENT, fromProject.map(project)))

fun Project.updated(): Project {
    projectUpdated(this)
    return this
}

fun Project.added(): Project {
    projectAdded(this)
    return this
}

fun Project.deleted(): Project {
    projectDeleted(this)
    return this
}

fun projectConsumer(): Flux<PlatformEvent> = projectBroker.consumer()