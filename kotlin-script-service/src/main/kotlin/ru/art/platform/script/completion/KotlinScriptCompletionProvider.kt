package ru.art.platform.script.completion

import com.intellij.openapi.util.*
import com.intellij.psi.*
import com.intellij.psi.tree.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.impl.*
import org.jetbrains.kotlin.idea.codeInsight.*
import org.jetbrains.kotlin.idea.util.*
import org.jetbrains.kotlin.lexer.*
import org.jetbrains.kotlin.lexer.KtTokens.*
import org.jetbrains.kotlin.name.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext.*
import org.jetbrains.kotlin.resolve.DescriptorUtils.*
import org.jetbrains.kotlin.resolve.scopes.*
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter.Companion.ALL
import org.jetbrains.kotlin.resolve.scopes.MemberScope.Companion.ALL_NAME_FILTER
import org.jetbrains.kotlin.types.expressions.*
import org.w3c.dom.bootstrap.DOMImplementationRegistry.*
import ru.art.core.constants.*
import ru.art.core.constants.StringConstants.*
import ru.art.platform.script.constants.KotlinScriptConstants.CLASS
import ru.art.platform.script.constants.KotlinScriptConstants.GENERIC_VALUE
import ru.art.platform.script.constants.KotlinScriptConstants.KOTLIN_SCRIPT_COMPILER_RENDERER
import ru.art.platform.script.constants.KotlinScriptConstants.KOTLIN_SCRIPT_COMPLETION_DELIMITER
import ru.art.platform.script.constants.KotlinScriptConstants.KOTLIN_SCRIPT_NUMBER_OF_CHAR_IN_COMPLETION_NAME
import ru.art.platform.script.constants.KotlinScriptConstants.KOTLIN_SCRIPT_NUMBER_OF_CHAR_IN_TAIL
import ru.art.platform.script.constants.KotlinScriptConstants.METHOD
import ru.art.platform.script.constants.KotlinScriptConstants.PACKAGE
import ru.art.platform.script.analyzer.KotlinScriptFileAnalyzer.analyzeFileForJvm
import ru.art.platform.script.exception.*
import ru.art.platform.script.facade.*
import ru.art.platform.script.factory.KotlinScriptPsiFileFactory.createPsiFile
import ru.art.platform.script.model.*
import java.beans.PropertyDescriptor
import java.util.*

class KotlinScriptCompletionProvider(private var allPsiFiles: MutableList<KtFile>,
                                     private var currentPsiFile: KtFile,
                                     private val lineNumber: Int,
                                     private val charNumber: Int) {
    private var currentPsiDocument = currentPsiFile.viewProvider.document
    private var caretPositionOffset: Int = 0
    private val expressionForScope: PsiElement?
        get() {
            var element = currentPsiFile.findElementAt(caretPositionOffset)
            while (element !is KtExpression && element != null) {
                element = element.parent
            }
            return element
        }


    @Synchronized
    fun getResult(): List<CompletionVariant> {
        try {
            addExpressionAtCaret()
            val resolveResult = analyzeFileForJvm(allPsiFiles)

            val analysisResult = resolveResult.first
            val containerProvider = resolveResult.second
            val bindingContext = analysisResult.bindingContext
            val moduleDescriptor = analysisResult.moduleDescriptor

            val element = expressionForScope as? KtElement ?: return emptyList()
            var descriptors: Collection<DeclarationDescriptor>? = null
            var isTipsManagerCompletion = true
            val resolutionFacade = KotlinScriptResolutionFacade(containerProvider, moduleDescriptor)
            val inDescriptor: DeclarationDescriptor = element.getResolutionScope(bindingContext, resolutionFacade).ownerDescriptor

            val helper = ReferenceVariantsHelper(bindingContext,
                    resolutionFacade,
                    analysisResult.moduleDescriptor,
                    KotlinScriptCompletionVisibilityFilter(inDescriptor, bindingContext, element, resolutionFacade),
                    emptySet())

            when {
                element is KtSimpleNameExpression -> {
                    descriptors = helper.getReferenceVariants(element, ALL,
                            { name: Name -> !name.isSpecial },
                            filterOutJavaGettersAndSetters = true,
                            filterOutShadowed = true,
                            excludeNonInitializedVariable = true,
                            useReceiverType = null)
                }
                element.parent is KtSimpleNameExpression -> {
                    descriptors = helper.getReferenceVariants(element.parent as KtSimpleNameExpression, ALL,
                            { name: Name -> !name.isSpecial },
                            filterOutJavaGettersAndSetters = true,
                            filterOutShadowed = true,
                            excludeNonInitializedVariable = true,
                            useReceiverType = null)
                }
                else -> {
                    isTipsManagerCompletion = false
                    val resolutionScope: LexicalScope?
                    when (val parent = element.parent) {
                        is KtQualifiedExpression -> {
                            val receiverExpression = parent.receiverExpression

                            val expressionType = bindingContext.get<KtExpression, KotlinTypeInfo>(EXPRESSION_TYPE_INFO, receiverExpression)!!.type
                            resolutionScope = bindingContext.get<KtElement, LexicalScope>(LEXICAL_SCOPE, receiverExpression)

                            if (expressionType != null && resolutionScope != null) {
                                descriptors = expressionType.memberScope.getContributedDescriptors(ALL, ALL_NAME_FILTER)
                            }
                        }
                        else -> {
                            resolutionScope = bindingContext.get<KtElement, LexicalScope>(LEXICAL_SCOPE, element as KtExpression)
                            if (resolutionScope != null) {
                                descriptors = resolutionScope.getContributedDescriptors(ALL, ALL_NAME_FILTER)
                            } else {
                                return emptyList()
                            }
                        }
                    }
                }
            }

            val result = ArrayList<CompletionVariant>()

            if (descriptors != null) {
                var prefix: String
                prefix = when {
                    !isTipsManagerCompletion -> {
                        element.parent.text
                    }
                    else -> {
                        element.text
                    }
                }
                prefix = prefix.substringBefore(KOTLIN_SCRIPT_COMPLETION_DELIMITER, prefix)
                if (prefix.endsWith(StringConstants.DOT)) {
                    prefix = EMPTY_STRING
                }

                if (descriptors !is ArrayList<*>) {
                    descriptors = ArrayList(descriptors)
                }

                (descriptors as ArrayList<DeclarationDescriptor>?)?.sortWith(Comparator { d1, d2 ->
                    val d1PresText = getPresentableText(d1)
                    val d2PresText = getPresentableText(d2)
                    (d1PresText.getFirst() + d1PresText.getSecond()).compareTo(d2PresText.getFirst() + d2PresText.getSecond(), ignoreCase = true)
                })

                for (descriptor in descriptors) {
                    val presentableText = getPresentableText(descriptor, element.isCallableReference())

                    val fullName = formatName(presentableText.getFirst(), KOTLIN_SCRIPT_NUMBER_OF_CHAR_IN_COMPLETION_NAME
                    )
                    var completionText = fullName
                    var position = completionText.indexOf(CharConstants.OPENING_BRACKET)
                    if (position != -1) {
                        //If this is a string with a package after
                        if (completionText[position - 1] == CharConstants.SPACE) {
                            position -= 2
                        }
                        //if this is a method without args
                        if (completionText[position + 1] == CharConstants.CLOSING_BRACES) {
                            position++
                        }
                        completionText = completionText.substring(0, position + 1)
                    }
                    position = completionText.indexOf(CharConstants.COLON)
                    if (position != -1) {
                        completionText = completionText.substring(0, position - 1)
                    }

                    if (prefix.isEmpty() || fullName.startsWith(prefix)) {
                        result.add(CompletionVariant(completionText, fullName, formatName(presentableText.getSecond(), KOTLIN_SCRIPT_NUMBER_OF_CHAR_IN_TAIL),
                                getIconFromDescriptor(descriptor)))
                    }
                }

                result.addAll(keywordsCompletionVariants(KEYWORDS, prefix))
                result.addAll(keywordsCompletionVariants(SOFT_KEYWORDS, prefix))
            }

            return result
        } catch (e: Throwable) {
            throw KotlinScriptCoreException(e)
        }

    }

    private fun getIconFromDescriptor(descriptor: DeclarationDescriptor): String = when (descriptor) {
        is FunctionDescriptor -> METHOD
        is PropertyDescriptor, is LocalVariableDescriptor -> PROPERTY
        is ClassDescriptor -> CLASS
        is PackageFragmentDescriptor, is PackageViewDescriptor -> PACKAGE
        is ValueParameterDescriptor -> GENERIC_VALUE
        is TypeParameterDescriptorImpl -> CLASS
        else -> EMPTY_STRING
    }

    private fun formatName(builder: String, symbols: Int): String = when {
        builder.length > symbols -> "${builder.substring(0, symbols)}..."
        else -> builder
    }

    private fun addExpressionAtCaret() {
        caretPositionOffset = getOffsetFromLineAndChar(lineNumber, charNumber)
        if (caretPositionOffset != 0) {
            val buffer = StringBuilder(currentPsiFile.text.substring(0, caretPositionOffset))
            buffer.append("$KOTLIN_SCRIPT_COMPLETION_DELIMITER ")
            buffer.append(currentPsiFile.text.substring(caretPositionOffset))
            allPsiFiles.remove(currentPsiFile)
            currentPsiFile = createPsiFile(KotlinScript(currentPsiFile.name, buffer.toString()))
            allPsiFiles.add(currentPsiFile)
            currentPsiDocument = currentPsiFile.viewProvider.document
        }
    }

    private fun getOffsetFromLineAndChar(line: Int, charNumber: Int): Int = currentPsiFile.viewProvider.document!!.getLineStartOffset(line) + charNumber

    private fun keywordsCompletionVariants(keywords: TokenSet, prefix: String): List<CompletionVariant> = keywords.types
            .map { (it as KtKeywordToken).value }
            .filter { it.startsWith(prefix) }
            .mapTo(ArrayList()) { CompletionVariant(it, it, EMPTY_STRING, EMPTY_STRING) }


    private fun getPresentableText(descriptor: DeclarationDescriptor, isCallableReferenceCompletion: Boolean = false): Pair<String, String> {
        var presentableText = when (descriptor) {
            is ConstructorDescriptor -> descriptor.constructedClass.name.asString()
            else -> descriptor.name.asString()
        }
        var typeText = EMPTY_STRING
        var tailText = EMPTY_STRING

        when (descriptor) {
            is FunctionDescriptor -> {
                val returnType = descriptor.returnType
                typeText = if (returnType != null) KOTLIN_SCRIPT_COMPILER_RENDERER.renderType(returnType) else EMPTY_STRING

                if (!isCallableReferenceCompletion) {
                    presentableText += KOTLIN_SCRIPT_COMPILER_RENDERER.renderFunctionParameters(descriptor)
                }
                val extensionFunction = descriptor.extensionReceiverParameter != null
                val containingDeclaration = descriptor.containingDeclaration
                if (extensionFunction) {
                    tailText += " for ${KOTLIN_SCRIPT_COMPILER_RENDERER.renderType(descriptor.extensionReceiverParameter!!.type)}"
                    tailText += " in ${getFqName(containingDeclaration)}"
                }
            }
            is VariableDescriptor -> {
                val outType = descriptor.type
                typeText = KOTLIN_SCRIPT_COMPILER_RENDERER.renderType(outType)
            }
            is ClassDescriptor -> {
                val declaredIn = descriptor.containingDeclaration
                tailText = " (${getFqName(declaredIn)})"
            }
            else -> {
                typeText = KOTLIN_SCRIPT_COMPILER_RENDERER.render(descriptor)
            }
        }

        return when {
            typeText.isEmpty() -> {
                Pair(presentableText, tailText)
            }
            else -> {
                Pair(presentableText, typeText)
            }
        }
    }

    private fun KtElement.isCallableReference() = parent is KtCallableReferenceExpression && this == (parent as KtCallableReferenceExpression).callableReference

}
