package ru.art.platform.state

import ru.art.platform.api.model.user.User

object SessionState {
    val localUser = ThreadLocal<User>()
}
