package ru.art.platform.script.completion

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.idea.core.*
import org.jetbrains.kotlin.idea.imports.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.*
import ru.art.platform.script.facade.*

private val excludedFromCompletion: List<String> = listOf(
        "kotlin.jvm.internal",
        "kotlin.coroutines.experimental.intrinsics",
        "kotlin.coroutines.intrinsics",
        "kotlin.coroutines.experimental.jvm.internal",
        "kotlin.coroutines.jvm.internal",
        "kotlin.reflect.jvm.internal"
)

class KotlinScriptCompletionVisibilityFilter(private val inDescriptor: DeclarationDescriptor,
                                             private val bindingContext: BindingContext,
                                             private val element: KtElement,
                                             private val resolutionFacade: KotlinScriptResolutionFacade) : (DeclarationDescriptor) -> Boolean {
        override fun invoke(descriptor: DeclarationDescriptor): Boolean {
            if (descriptor is TypeParameterDescriptor && !isTypeParameterVisible(descriptor)) return false

            if (descriptor is DeclarationDescriptorWithVisibility) {
                return descriptor.isVisible(element, null, bindingContext, resolutionFacade)
            }

            if (descriptor.isInternalImplementationDetail()) return false

            return true
        }

        private fun isTypeParameterVisible(typeParameter: TypeParameterDescriptor): Boolean {
            val owner = typeParameter.containingDeclaration
            var parent: DeclarationDescriptor? = inDescriptor
            while (parent != null) {
                if (parent == owner) return true
                if (parent is ClassDescriptor && !parent.isInner) return false
                parent = parent.containingDeclaration
            }
            return true
        }

        private fun DeclarationDescriptor.isInternalImplementationDetail(): Boolean = importableFqName?.asString() in excludedFromCompletion
    }