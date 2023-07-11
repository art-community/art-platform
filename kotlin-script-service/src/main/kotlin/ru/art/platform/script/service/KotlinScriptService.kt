package ru.art.platform.script.service

import org.jetbrains.kotlin.descriptors.runtime.structure.*
import ru.art.platform.script.analyzer.*
import ru.art.platform.script.compiler.KotlinScriptCompiler.compile
import ru.art.platform.script.completion.*
import ru.art.platform.script.constants.KotlinScriptConstants.CALL
import ru.art.platform.script.constants.KotlinScriptConstants.KOTLIN_DEFAULT_SCRIPT_NAME
import ru.art.platform.script.constants.KotlinScriptConstants.KT
import ru.art.platform.script.constants.KotlinScriptConstants.KT_DOT_CLASS
import ru.art.platform.script.constants.KotlinScriptConstants.RUN
import ru.art.platform.script.factory.KotlinScriptPsiFileFactory.createPsiFile
import ru.art.platform.script.factory.KotlinScriptPsiFileFactory.createPsiFiles
import ru.art.platform.script.loader.KotlinScriptClassLoader.initializeScriptClass
import ru.art.platform.script.model.*

fun String.toKotlinScript(name: String = KOTLIN_DEFAULT_SCRIPT_NAME) = KotlinScript(name, this)

object KotlinScriptService {
    @Suppress("UNCHECKED_CAST")
    fun <T> callKotlinScript(script: KotlinScript, function: String = CALL, vararg parameters: Any): T? =
            compileKotlinScript(script).files["${script.name.capitalize()}$KT_DOT_CLASS"]
                    ?.let { initializeScriptClass("${script.name.capitalize()}$KT", it) }
                    ?.run {
                        if (parameters.isEmpty()) {
                            return@run getDeclaredMethod(function).invoke(this)
                        }
                        val method = declaredMethods.single { method ->
                            method.parameterTypes.map { types -> if (types.isPrimitive) types.wrapperByPrimitive!! else types } == parameters.map { parameter -> parameter.javaClass }
                        }
                        return@run method.invoke(this, *parameters)
                    } as T?

    fun runKotlinScript(script: KotlinScript, function: String = RUN) {
        compileKotlinScript(script).files["${script.name.capitalize()}$KT_DOT_CLASS"]
                ?.let { initializeScriptClass("${script.name.capitalize()}$KT", it) }
                ?.apply { getDeclaredMethod(function).invoke(this) }
    }

    fun getKotlinScriptErrors(script: KotlinScript): Map<String, List<ErrorDescriptor>> =
            getKotlinScriptsErrors(mapOf(script.name to script))

    fun getKotlinScriptsErrors(scripts: Map<String, KotlinScript>): Map<String, List<ErrorDescriptor>> =
            KotlinScriptErrorsAnalyzer(createPsiFiles(scripts)).getAllErrors()

    fun getKotlinScriptCompletionVariants(currentScript: KotlinScript, lineNumber: Int, caretOffset: Int): List<CompletionVariant> =
            getKotlinScriptCompletionVariants(mapOf(currentScript.name to currentScript), currentScript, lineNumber, caretOffset)

    fun getKotlinScriptCompletionVariants(scripts: Map<String, KotlinScript>, currentScript: KotlinScript, lineNumber: Int, caretOffset: Int): List<CompletionVariant> =
            KotlinScriptCompletionProvider(createPsiFiles(scripts), createPsiFile(currentScript), lineNumber, caretOffset).getResult()

    fun compileKotlinScript(script: KotlinScript): CompilationResult =
            compileKotlinScripts(mapOf(script.name to script))

    fun compileKotlinScripts(scripts: Map<String, KotlinScript>): CompilationResult =
            compile(createPsiFiles(scripts))
}