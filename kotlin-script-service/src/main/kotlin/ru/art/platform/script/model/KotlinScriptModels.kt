package ru.art.platform.script.model

import ru.art.platform.script.constants.KotlinScriptConstants.IMPORT
import ru.art.platform.script.constants.KotlinScriptConstants.KOTLIN_DEFAULT_SCRIPT_NAME
import kotlin.reflect.*

data class CompilationResult(val files: Map<String, ByteArray>)

data class CompletionVariant(val text: String, val displayText: String, val tail: String, val icon: String)

data class ErrorDescriptor(val interval: TextInterval, val message: String, val severity: Severity, var className: String?) {
    init {
        className = className ?: severity.name
    }
}

data class TextInterval(val start: TextPosition, val end: TextPosition)

data class TextPosition(val line: Int, val ch: Int)

class KotlinScript(val name: String = KOTLIN_DEFAULT_SCRIPT_NAME, content: String) {
    private val imports: MutableList<String> = mutableListOf()
    private val rawContent: String = content
    val content: String
        get() = imports.joinToString { "$IMPORT $it;" } + rawContent

    fun importClass(clazz: Class<*>): KotlinScript {
        return import(clazz.name)
    }

    fun importClass(kClass: KClass<*>): KotlinScript {
        return import(kClass.java.name)
    }

    fun importPackageContent(pkg: String): KotlinScript {
        return import("$pkg.*")
    }

    fun importClassPackage(clazz: Class<*>): KotlinScript {
        return import(clazz.`package`.name)
    }

    fun importClassPackage(kClass: KClass<*>): KotlinScript {
        return import(kClass.java.`package`.name)
    }

    fun importClassPackageContent(clazz: Class<*>): KotlinScript {
        return import("${clazz.`package`.name}.*")
    }

    fun importClassPackageContent(kClass: KClass<*>): KotlinScript {
        return import("${kClass.java.`package`.name}.*")
    }

    fun importClassContent(clazz: Class<*>): KotlinScript {
        return import("${clazz.name}.*")
    }

    fun importClassContent(kClass: KClass<*>): KotlinScript {
        return import("${kClass.java.name}.*")
    }

    fun import(import: String): KotlinScript {
        imports += import
        return this
    }
}

enum class Severity {
    INFO, ERROR, WARNING
}