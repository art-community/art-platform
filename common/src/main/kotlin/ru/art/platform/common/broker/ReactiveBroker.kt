package ru.art.platform.common.broker

import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.create
import reactor.core.publisher.FluxSink
import reactor.core.scheduler.Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE
import reactor.core.scheduler.Schedulers.newBoundedElastic
import ru.art.core.constants.ThreadConstants.DEFAULT_THREAD_POOL_SIZE
import ru.art.entity.Entity
import ru.art.entity.Entity.entityBuilder
import ru.art.entity.Value
import ru.art.entity.Value.asEntity
import ru.art.platform.common.constants.CommonConstants.BROKER_PREFETCH
import java.util.concurrent.CopyOnWriteArrayList

class PlatformEvent(val type: String, val data: Entity)

object PlatformEventMapper {
    private const val type = "type"
    private const val data = "data"

    val toPlatformEvent = { value: Value -> PlatformEvent(asEntity(value).getString(type), asEntity(value).getEntity(data)) }

    val fromPlatformEvent = { event: PlatformEvent -> entityBuilder().stringField(type, event.type).entityField(data, event.data).build() }
}

class ReactiveBroker {
    private var consumers = CopyOnWriteArrayList<FluxSink<PlatformEvent>>()
    private val publishingPool = newBoundedElastic(DEFAULT_THREAD_POOL_SIZE, DEFAULT_BOUNDED_ELASTIC_QUEUESIZE, "${ReactiveBroker::class.java.name}-publishing")

    fun consumer(): Flux<PlatformEvent> =
            create<PlatformEvent> { emitter -> consumers.add(emitter.onCancel { consumers.remove(emitter) }) }.publishOn(publishingPool, BROKER_PREFETCH)

    fun emit(event: PlatformEvent) {
        consumers.parallelStream().forEach { emitter -> emitter.next(event) }
    }

    fun error(error: Throwable) {
        consumers.parallelStream().forEach { emitter -> emitter.error(error) }
    }
}
