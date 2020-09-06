package ru.art.platform.extensions

fun <E> Collection<E>.ifNotEmpty(action: (collection: Collection<E>) -> Unit) {
    if (isNotEmpty()) {
        action(this)
    }
}
