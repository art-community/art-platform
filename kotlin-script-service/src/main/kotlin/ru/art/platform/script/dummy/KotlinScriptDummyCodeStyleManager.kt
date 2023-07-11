package ru.art.platform.script.dummy

import com.intellij.lang.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.fileTypes.*
import com.intellij.openapi.project.*
import com.intellij.openapi.util.*
import com.intellij.psi.*
import com.intellij.psi.codeStyle.*
import com.intellij.util.*

object KotlinScriptDummyCodeStyleManager : CodeStyleManager() {
    override fun getProject(): Project = throw UnsupportedOperationException()

    @Throws(IncorrectOperationException::class)
    override fun reformat(psiElement: PsiElement): PsiElement = psiElement

    @Throws(IncorrectOperationException::class)
    override fun reformat(psiElement: PsiElement, b: Boolean): PsiElement = psiElement

    @Throws(IncorrectOperationException::class)
    override fun reformatRange(psiElement: PsiElement, i: Int, i1: Int): PsiElement = psiElement

    @Throws(IncorrectOperationException::class)
    override fun reformatRange(psiElement: PsiElement, i: Int, i1: Int, b: Boolean): PsiElement = psiElement

    @Throws(IncorrectOperationException::class)
    override fun reformatText(psiFile: PsiFile, i: Int, i1: Int) = Unit

    @Throws(IncorrectOperationException::class)
    override fun reformatText(psiFile: PsiFile, collection: Collection<TextRange>) = Unit

    @Throws(IncorrectOperationException::class)
    override fun reformatTextWithContext(psiFile: PsiFile, changedRangesInfo: ChangedRangesInfo) = Unit

    @Throws(IncorrectOperationException::class)
    override fun reformatTextWithContext(psiFile: PsiFile, collection: Collection<TextRange>) = Unit

    @Throws(IncorrectOperationException::class)
    override fun adjustLineIndent(psiFile: PsiFile, textRange: TextRange) = Unit

    @Throws(IncorrectOperationException::class)
    override fun adjustLineIndent(psiFile: PsiFile, i: Int): Int = i

    override fun adjustLineIndent(document: Document, i: Int): Int = i

    override fun isLineToBeIndented(psiFile: PsiFile, i: Int): Boolean = false

    override fun getLineIndent(psiFile: PsiFile, i: Int): String? = null

    override fun getLineIndent(document: Document, i: Int): String? = null

    @Suppress("Deprecation")
    override fun getIndent(s: String, fileType: FileType): Indent? = null

    @Suppress("Deprecation")
    override fun fillIndent(indent: Indent, fileType: FileType): String? = null

    @Suppress("Deprecation")
    override fun zeroIndent(): Indent? = null

    @Throws(IncorrectOperationException::class)
    override fun reformatNewlyAddedElement(astNode: ASTNode, astNode1: ASTNode) = Unit

    override fun isSequentialProcessingAllowed(): Boolean = false

    override fun performActionWithFormatterDisabled(runnable: Runnable) = runnable.run()

    override fun <T : Throwable?> performActionWithFormatterDisabled(throwableRunnable: ThrowableRunnable<T>) = throwableRunnable.run()

    override fun <T> performActionWithFormatterDisabled(computable: Computable<T>): T = computable.compute()
}