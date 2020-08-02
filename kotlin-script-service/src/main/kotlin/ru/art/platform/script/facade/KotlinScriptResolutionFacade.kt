package ru.art.platform.script.facade

import com.intellij.psi.*
import org.jetbrains.kotlin.analyzer.*
import org.jetbrains.kotlin.container.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.idea.resolve.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.lazy.*
import ru.art.platform.script.environment.KotlinScriptEnvironmentManager.KOTLIN_SCRIPT_ENVIRONMENT

class KotlinScriptResolutionFacade(private val provider: ComponentProvider?, override val moduleDescriptor: ModuleDescriptor) : ResolutionFacade {
    override fun <T : Any> getFrontendService(element: PsiElement, serviceClass: Class<T>): T = throw UnsupportedOperationException()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getFrontendService(serviceClass: Class<T>): T = when (provider) {
        null -> throw IllegalArgumentException("Provider is null in 'getFrontendService'")
        else -> provider.resolve(serviceClass)!!.getValue() as T
    }

    override fun <T : Any> getFrontendService(moduleDescriptor: ModuleDescriptor, serviceClass: Class<T>): T = throw UnsupportedOperationException()

    override fun <T : Any> getIdeService(serviceClass: Class<T>): T = throw UnsupportedOperationException()

    override fun <T : Any> tryGetFrontendService(element: PsiElement, serviceClass: Class<T>): T? = throw UnsupportedOperationException()

    override val project = KOTLIN_SCRIPT_ENVIRONMENT.project

    override fun analyze(element: KtElement, bodyResolveMode: BodyResolveMode): BindingContext = throw UnsupportedOperationException()

    override fun analyzeWithAllCompilerChecks(elements: Collection<KtElement>): AnalysisResult = throw UnsupportedOperationException()

    override fun resolveToDescriptor(declaration: KtDeclaration, bodyResolveMode: BodyResolveMode): DeclarationDescriptor = throw UnsupportedOperationException()

    override fun analyze(elements: Collection<KtElement>, bodyResolveMode: BodyResolveMode): BindingContext = throw UnsupportedOperationException()

}
