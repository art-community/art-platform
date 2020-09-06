package ru.art.platform.panel.extensions

import org.reactivestreams.Subscription
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.art.task.deferred.executor.SchedulerModuleActions.asynchronous
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

fun <T> Mono<T>.get(): T? = asynchronous<T> { return@asynchronous block() }.get()

class ChunkedRequester(val count: Int) {
    private lateinit var requester: () -> Unit;

    fun activate(requester: () -> Unit) {
        this.requester = requester
    }

    fun request() = this.requester()
}

fun <T> Flux<T>.chunked(chunksCount: Int): Flux<T> {
    val chunksCounter = AtomicInteger(chunksCount)
    val sourceSubscription = AtomicReference<Subscription>()
    return doOnNext {
        if (chunksCounter.decrementAndGet() == chunksCount / 2) {
            chunksCounter.addAndGet(chunksCount)
            sourceSubscription.get().request(chunksCount.toLong())
        }
    }.doOnSubscribe { subscription ->
        sourceSubscription.set(subscription)
        subscription.request(chunksCount.toLong())
    }

}

fun <T> Flux<T>.chunked(requester: ChunkedRequester): Flux<T> {
    val chunksCounter = AtomicInteger(requester.count)
    val sourceSubscription = AtomicReference<Subscription>()
    requester.activate {
        val passed = chunksCounter.decrementAndGet()
        if (passed == requester.count / 2) {
            chunksCounter.addAndGet(requester.count)
            sourceSubscription.get().request(requester.count.toLong())
        }
    }
    return doOnSubscribe { subscription ->
        sourceSubscription.set(subscription)
        sourceSubscription.get().request(requester.count.toLong())
    }
}
