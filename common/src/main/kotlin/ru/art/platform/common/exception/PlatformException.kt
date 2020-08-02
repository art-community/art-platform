package ru.art.platform.common.exception

import ru.art.platform.common.constants.ErrorCodes.*

class PlatformException : RuntimeException {
    val code: String

    constructor(code: String = PLATFORM_ERROR) : super(code) {
        this.code = code
    }

    constructor(code: String = PLATFORM_ERROR, message: String) : super(message) {
        this.code = code
    }

    constructor(code: String = PLATFORM_ERROR, message: String, throwable: Throwable) : super(message, throwable) {
        this.code = code
    }

    constructor(code: String = PLATFORM_ERROR, throwable: Throwable) : super(throwable) {
        this.code = code
    }
}
