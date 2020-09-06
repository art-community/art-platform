package ru.art.platform.panel.state

import ru.art.platform.api.model.user.User

object SessionState {
    val localUser = ThreadLocal<User>()
}
