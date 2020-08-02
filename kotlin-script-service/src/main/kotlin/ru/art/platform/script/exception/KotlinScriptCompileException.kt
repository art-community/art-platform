package ru.art.platform.script.exception

class KotlinScriptCompileException(private val e: Throwable) : RuntimeException(e) {
    override val message: String
        get() = e.message!!
}