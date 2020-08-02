package ru.art.platform.script.exception

class KotlinScriptCoreException(private val e: Throwable) : RuntimeException(e) {
    override val message: String
        get() = e.message!!

}