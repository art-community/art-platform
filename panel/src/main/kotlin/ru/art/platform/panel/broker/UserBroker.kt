package ru.art.platform.panel.broker

import reactor.core.publisher.*
import ru.art.platform.api.mapping.project.ProjectMapper.*
import ru.art.platform.api.mapping.user.UserMapper.fromUser
import ru.art.platform.api.model.project.*
import ru.art.platform.api.model.user.User
import ru.art.platform.common.broker.*
import ru.art.platform.common.constants.EventTypes.*

private val userBroker = ReactiveBroker()

fun userAdded(user: User) = userBroker.emit(PlatformEvent(ADD_EVENT, fromUser.map(user)))

fun userUpdated(user: User) = userBroker.emit(PlatformEvent(UPDATE_EVENT, fromUser.map(user)))

fun userDeleted(user: User) = userBroker.emit(PlatformEvent(DELETE_EVENT, fromUser.map(user)))

fun User.updated(): User {
    userUpdated(this)
    return this
}

fun User.added(): User {
    userAdded(this)
    return this
}

fun User.deleted(): User {
    userDeleted(this)
    return this
}

fun userConsumer(): Flux<PlatformEvent> = userBroker.consumer()
