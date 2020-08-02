package ru.art.platform.common.emitter

import java.lang.Runtime.*
import java.util.concurrent.atomic.*

fun <T> simpleEmitter(emitter: (T) -> Unit = {}) = Emitter(emitter, {}, {})
fun <T> emitterWithErrors(emitter: (T) -> Unit = {}, errorEmitter: (Throwable) -> Unit = {}) = Emitter(emitter, errorEmitter, {})
fun <T> emitter(emitter: (T) -> Unit = {}, errorEmitter: (Throwable) -> Unit = {}, completer: () -> Unit = {}) = Emitter(emitter, errorEmitter, completer)

class Emitter<T>(private val emitter: (T) -> Unit, private val errorEmitter: (Throwable) -> Unit, private val completer: () -> Unit = {}) {
    private var completed = AtomicBoolean()

    fun emit(value: T) {
        emitter(value)
    }

    fun emitWithError(value: T, throwable: Throwable) {
        emit(value)
        emitError(throwable)
        completer()
    }

    fun emitWithCompletion(value: T) {
        emit(value)
        complete()
    }

    fun emitError(throwable: Throwable) {
        errorEmitter(throwable)
    }

    fun complete() {
        if (!completed.get()) {
            completer()
            getRuntime().gc()
            completed.set(true)
        }
    }

    fun completeWithError(throwable: Throwable) {
        emitError(throwable)
        complete()
    }
}