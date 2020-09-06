package ru.art.platform.panel.extensions

fun <E> Collection<E>.ifNotEmpty(action: (collection: Collection<E>) -> Unit) {
    if (isNotEmpty()) {
        action(this)
    }
}
