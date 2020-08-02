package ru.art.platform.script.factory

import com.intellij.openapi.vfs.CharsetToolkit.*
import com.intellij.psi.PsiFileFactory.*
import com.intellij.psi.impl.*
import com.intellij.testFramework.*
import org.jetbrains.kotlin.idea.KotlinLanguage.*
import org.jetbrains.kotlin.psi.*
import ru.art.platform.script.constants.KotlinScriptConstants.DOT_KT
import ru.art.platform.script.environment.KotlinScriptEnvironmentManager.KOTLIN_SCRIPT_ENVIRONMENT
import ru.art.platform.script.model.*
import java.util.*

object KotlinScriptPsiFileFactory {
    fun createPsiFile(script: KotlinScript): KtFile {
        val project = KOTLIN_SCRIPT_ENVIRONMENT.project
        val nameWithExtension = if (script.name.endsWith(DOT_KT)) script.name else "${script.name}$DOT_KT"
        val virtualFile = LightVirtualFile(nameWithExtension, INSTANCE, script.content)
        virtualFile.charset = UTF8_CHARSET
        return (getInstance(project) as PsiFileFactoryImpl).trySetupPsiForFile(virtualFile, INSTANCE, true, false) as KtFile
    }

    fun createPsiFiles(files: Map<String, KotlinScript>): MutableList<KtFile> = files.keys.mapTo(ArrayList()) { createPsiFile(files.getValue(it)) }
}
