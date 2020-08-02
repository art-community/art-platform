package ru.art.platform.common.emitter

import reactor.core.publisher.*
import reactor.core.publisher.Flux.*
import reactor.core.publisher.FluxSink.OverflowStrategy.*

class ReactiveEmitter<EventMutatorType, EventType>(private val emitter: FluxSink<EventType>,
                                                   private var current: EventType,
                                                   private val fromEventMapper: (EventType) -> EventMutatorType,
                                                   private val toEventMapper: (EventMutatorType) -> EventType) {
    fun current() = current

    fun emit(mutator: EventMutatorType.() -> EventMutatorType = { this }): EventType {
        current = toEventMapper(fromEventMapper(current).mutator())
        emitter.next(current)
        return current
    }

    fun emitWithError(error: Throwable, mutator: EventMutatorType.() -> EventMutatorType = { this }): EventType {
        current = toEventMapper(fromEventMapper(current).mutator())
        emitter.next(current)
        emitter.error(error)
        return current
    }

    fun emitWithCompletion(mutator: EventMutatorType.() -> EventMutatorType = { this }): EventType {
        current = toEventMapper(fromEventMapper(current).mutator())
        emitter.next(current)
        emitter.complete()
        return current
    }

    fun emitError(error: Throwable, mutator: EventMutatorType.() -> EventMutatorType = { this }): EventType {
        current = toEventMapper(fromEventMapper(current).mutator())
        emitter.error(error)
        return current
    }

    fun complete(): EventType {
        emitter.complete()
        return current
    }

    fun completeWithError(error: Throwable, mutator: EventMutatorType.() -> EventMutatorType = { this }): EventType {
        current = toEventMapper(fromEventMapper(current).mutator())
        emitter.error(error)
        emitter.complete()
        return current
    }
}

class ReactiveEmitterBuilder<EventType, EventMutatorType>(private val base: EventType) {
    private lateinit var fromEventMapper: (EventType) -> EventMutatorType
    private lateinit var toEventMapper: (EventMutatorType) -> EventType

    fun to(mapper: EventType.() -> EventMutatorType): ReactiveEmitterBuilder<EventType, EventMutatorType> {
        this.fromEventMapper = mapper
        return this
    }

    fun from(mapper: EventMutatorType.() -> EventType): ReactiveEmitterBuilder<EventType, EventMutatorType> {
        this.toEventMapper = mapper
        return this
    }

    fun start(mutation: ReactiveEmitter<EventMutatorType, EventType>.() -> Unit): Flux<EventType> =
            create<EventType> { emitter -> mutation(ReactiveEmitter(emitter, base, fromEventMapper, toEventMapper)) }

    fun defer(mutation: ReactiveEmitter<EventMutatorType, EventType>.() -> Unit): Flux<EventType> =
            Flux.defer { start(mutation) }
}

fun <EventType, EventMutatorType> reactiveEmitter(base: EventType) = ReactiveEmitterBuilder<EventType, EventMutatorType>(base)