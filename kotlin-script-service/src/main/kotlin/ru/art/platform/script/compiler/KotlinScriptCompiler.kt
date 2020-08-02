package ru.art.platform.script.compiler

import org.jetbrains.kotlin.codegen.ClassBuilderFactories.*
import org.jetbrains.kotlin.codegen.KotlinCodegenFacade.*
import org.jetbrains.kotlin.codegen.state.*
import org.jetbrains.kotlin.psi.*
import ru.art.logging.LoggingModule.*
import ru.art.platform.script.analyzer.KotlinScriptFileAnalyzer.analyzeFileForJvm
import ru.art.platform.script.environment.KotlinScriptEnvironmentManager.KOTLIN_SCRIPT_ENVIRONMENT
import ru.art.platform.script.exception.*
import ru.art.platform.script.model.*

object KotlinScriptCompiler {
    fun compile(currentPsiFiles: List<KtFile>): CompilationResult {
        try {
            val analyzeExhaust = analyzeFileForJvm(currentPsiFiles).getFirst()
            val generationState = GenerationState.Builder(KOTLIN_SCRIPT_ENVIRONMENT.project,
                    BINARIES,
                    analyzeExhaust.moduleDescriptor,
                    analyzeExhaust.bindingContext,
                    currentPsiFiles,
                    KOTLIN_SCRIPT_ENVIRONMENT.configuration)
                    .build()
            compileCorrectFiles(generationState) { throwable, fileUrl ->
                loggingModule().getLogger(KotlinScriptCompiler.javaClass).error("Compilation error at file $fileUrl", throwable)
            }
            val files = mutableMapOf<String, ByteArray>()
            for (file in generationState.factory.asList()) {
                files[file.relativePath] = file.asByteArray()
            }
            return CompilationResult(files)
        } catch (e: Throwable) {
            throw KotlinScriptCompileException(e)
        }

    }
}
