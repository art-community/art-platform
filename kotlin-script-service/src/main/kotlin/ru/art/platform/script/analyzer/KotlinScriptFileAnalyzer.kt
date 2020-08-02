package ru.art.platform.script.analyzer

import com.intellij.openapi.util.*
import org.jetbrains.kotlin.analyzer.*
import org.jetbrains.kotlin.analyzer.AnalysisResult.Companion.success
import org.jetbrains.kotlin.cli.jvm.compiler.*
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM.createContainer
import org.jetbrains.kotlin.container.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo.Companion.EMPTY
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension.Companion.getInstances
import org.jetbrains.kotlin.resolve.lazy.declarations.*
import ru.art.platform.script.environment.KotlinScriptEnvironmentManager.KOTLIN_SCRIPT_ENVIRONMENT

object KotlinScriptFileAnalyzer {
    @Synchronized
    fun analyzeFileForJvm(files: List<KtFile>): Pair<AnalysisResult, ComponentProvider> {
        val trace = CliBindingTrace()
        val configuration = KOTLIN_SCRIPT_ENVIRONMENT.configuration
        val container = createContainer(
                KOTLIN_SCRIPT_ENVIRONMENT.project,
                files,
                trace,
                configuration,
                { globalSearchScope -> KOTLIN_SCRIPT_ENVIRONMENT.createPackagePartProvider(globalSearchScope) },
                { storageManager, ktFiles -> FileBasedDeclarationProviderFactory(storageManager, ktFiles) }
        )

        container.getService(LazyTopDownAnalyzer::class.java).analyzeDeclarations(TopDownAnalysisMode.TopLevelDeclarations, files, EMPTY)

        val moduleDescriptor = container.getService(ModuleDescriptor::class.java)
        for (extension in getInstances(KOTLIN_SCRIPT_ENVIRONMENT.project)) {
            val result = extension.analysisCompleted(KOTLIN_SCRIPT_ENVIRONMENT.project, moduleDescriptor, trace, files)
            if (result != null) break
        }

        return Pair(success(trace.bindingContext, moduleDescriptor), container)
    }
}
