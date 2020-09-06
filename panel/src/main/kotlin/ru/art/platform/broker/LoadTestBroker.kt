package ru.art.platform.broker

import ru.art.platform.api.mapping.load.LoadTestMapper.*
import ru.art.platform.api.model.load.*
import ru.art.platform.common.broker.*
import ru.art.platform.common.constants.EventTypes.*

private val loadTestBroker = ReactiveBroker()

fun loadTestAdded(LoadTest: LoadTest) = loadTestBroker.emit(PlatformEvent(ADD_EVENT, fromLoadTest.map(LoadTest)))

fun loadTestUpdated(LoadTest: LoadTest) = loadTestBroker.emit(PlatformEvent(UPDATE_EVENT, fromLoadTest.map(LoadTest)))

fun loadTestDeleted(LoadTest: LoadTest) = loadTestBroker.emit(PlatformEvent(DELETE_EVENT, fromLoadTest.map(LoadTest)))

fun LoadTest.updated(): LoadTest {
    loadTestUpdated(this)
    return this
}

fun LoadTest.added(): LoadTest {
    loadTestAdded(this)
    return this
}

fun LoadTest.deleted(): LoadTest {
    loadTestDeleted(this)
    return this
}

fun loadTestConsumer() = loadTestBroker.consumer()