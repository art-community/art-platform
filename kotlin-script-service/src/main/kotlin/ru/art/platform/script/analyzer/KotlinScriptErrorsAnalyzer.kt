package ru.art.platform.script.analyzer

import com.intellij.openapi.editor.*
import com.intellij.psi.*
import org.jetbrains.kotlin.diagnostics.*
import org.jetbrains.kotlin.diagnostics.Errors.*
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.diagnostics.Severity.*
import org.jetbrains.kotlin.diagnostics.rendering.*
import org.jetbrains.kotlin.psi.*
import ru.art.platform.script.constants.KotlinScriptConstants.KOTLIN_SCRIPT_ERROR_MESSAGE
import ru.art.platform.script.constants.KotlinScriptConstants.KOTLIN_SCRIPT_RED_WAVY_LINE
import ru.art.platform.script.analyzer.KotlinScriptFileAnalyzer.analyzeFileForJvm
import ru.art.platform.script.exception.*
import ru.art.platform.script.model.*
import java.util.*
import java.util.Collections.*

class KotlinScriptErrorsAnalyzer(private val currentPsiFiles: List<KtFile>) {
    fun getAllErrors(): Map<String, List<ErrorDescriptor>> {
        try {
            val errors = HashMap<String, List<ErrorDescriptor>>()
            for (psiFile in currentPsiFiles) {
                errors[psiFile.name] = getErrorsByVisitor(psiFile)
            }
            errors.putAll(getErrorsFromDiagnostics(analyzeFileForJvm(currentPsiFiles).getFirst().bindingContext.diagnostics.all(), errors))
            return errors
        } catch (e: Throwable) {
            throw KotlinScriptCoreException(e)
        }

    }

    private fun getErrorsFromDiagnostics(diagnostics: Collection<Diagnostic>, errors: Map<String, List<ErrorDescriptor>>): Map<String, List<ErrorDescriptor>> {
        val diagnosticErrors: MutableMap<String, List<ErrorDescriptor>> = errors.toMutableMap()
        try {
            for (diagnostic in diagnostics) {
                diagnostic.psiFile.virtualFile ?: continue
                val render = DefaultErrorMessages.render(diagnostic)
                if (render.contains(KOTLIN_SCRIPT_ERROR_MESSAGE)) {
                    continue
                }
                if (diagnostic.severity != INFO) {
                    val textRangeIterator = diagnostic.textRanges.iterator()
                    if (!textRangeIterator.hasNext()) {
                        continue
                    }
                    val firstRange = textRangeIterator.next()

                    var className = diagnostic.severity.name
                    if (!(diagnostic.factory === UNRESOLVED_REFERENCE) && diagnostic.severity == ERROR) {
                        className = KOTLIN_SCRIPT_RED_WAVY_LINE
                    }
                    val interval = getInterval(firstRange.startOffset, firstRange.endOffset, diagnostic.psiFile.viewProvider.document!!)
                    val currentError = diagnosticErrors[diagnostic.psiFile.name]!!.plus(ErrorDescriptor(interval, render, convertSeverity(diagnostic.severity), className))
                    diagnosticErrors[diagnostic.psiFile.name] = currentError
                }
            }

            for (key in diagnosticErrors.keys) {
                sort(diagnosticErrors[key]!!, Comparator { o1, o2 ->
                    when {
                        o1.interval.start.line > o2.interval.start.line -> return@Comparator 1
                        o1.interval.start.line < o2.interval.start.line -> return@Comparator -1
                        o1.interval.start.line == o2.interval.start.line -> when {
                            o1.interval.start.ch > o2.interval.start.ch -> return@Comparator 1
                            o1.interval.start.ch < o2.interval.start.ch -> return@Comparator -1
                            o1.interval.start.ch == o2.interval.start.ch -> return@Comparator 0
                            else -> {
                            }
                        }
                    }
                    -1
                })
            }
        } catch (e: Throwable) {
            throw KotlinScriptCoreException(e)
        }
        return diagnosticErrors
    }

    private fun getErrorsByVisitor(psiFile: PsiFile): List<ErrorDescriptor> {
        val errorElements = ArrayList<PsiErrorElement>()
        val visitor = object : PsiElementVisitor() {
            override fun visitElement(element: PsiElement) {
                element.acceptChildren(this)
            }

            override fun visitErrorElement(element: PsiErrorElement) {
                errorElements.add(element)
            }

        }

        val errors = ArrayList<ErrorDescriptor>()
        visitor.visitFile(psiFile)
        for (errorElement in errorElements) {
            val start = errorElement.textRange.startOffset
            val end = errorElement.textRange.endOffset
            val interval = getInterval(start, end, psiFile.viewProvider.document!!)
            errors.add(ErrorDescriptor(interval, errorElement.errorDescription, convertSeverity(ERROR), KOTLIN_SCRIPT_RED_WAVY_LINE))
        }
        return errors
    }

    private fun convertSeverity(severity: Severity): ru.art.platform.script.model.Severity = when (severity) {
        ERROR -> ru.art.platform.script.model.Severity.ERROR
        INFO -> ru.art.platform.script.model.Severity.INFO
        WARNING -> ru.art.platform.script.model.Severity.WARNING
    }

    private fun getInterval(start: Int, end: Int, currentDocument: Document): TextInterval {
        val lineNumberForElementStart = currentDocument.getLineNumber(start)
        val lineNumberForElementEnd = currentDocument.getLineNumber(end)
        var charNumberForElementStart = start - currentDocument.getLineStartOffset(lineNumberForElementStart)
        var charNumberForElementEnd = end - currentDocument.getLineStartOffset(lineNumberForElementStart)
        if (start == end && lineNumberForElementStart == lineNumberForElementEnd) {
            charNumberForElementStart--
            if (charNumberForElementStart < 0) {
                charNumberForElementStart++
                charNumberForElementEnd++
            }
        }
        val startPosition = TextPosition(lineNumberForElementStart, charNumberForElementStart)
        val endPosition = TextPosition(lineNumberForElementEnd, charNumberForElementEnd)
        return TextInterval(startPosition, endPosition)
    }
}
