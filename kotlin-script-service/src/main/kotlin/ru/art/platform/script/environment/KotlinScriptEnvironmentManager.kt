package ru.art.platform.script.environment

import com.intellij.codeInsight.*
import com.intellij.codeInsight.runner.*
import com.intellij.core.CoreApplicationEnvironment.*
import com.intellij.mock.*
import com.intellij.openapi.*
import com.intellij.openapi.extensions.*
import com.intellij.openapi.extensions.Extensions.*
import com.intellij.openapi.fileTypes.*
import com.intellij.psi.*
import com.intellij.psi.augment.*
import com.intellij.psi.codeStyle.*
import com.intellij.psi.compiled.*
import com.intellij.psi.impl.compiled.*
import com.intellij.psi.meta.*
import com.intellij.psi.stubs.*
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys.*
import org.jetbrains.kotlin.cli.common.arguments.*
import org.jetbrains.kotlin.cli.common.environment.*
import org.jetbrains.kotlin.cli.common.messages.MessageCollector.Companion.NONE
import org.jetbrains.kotlin.cli.jvm.compiler.*
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles.*
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment.Companion.createForProduction
import org.jetbrains.kotlin.cli.jvm.config.*
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.config.CommonConfigurationKeys.MODULE_NAME
import org.jetbrains.kotlin.config.JVMConfigurationKeys.*
import org.jetbrains.kotlin.js.config.JSConfigurationKeys.*
import org.jetbrains.kotlin.utils.PathUtil.getJdkClassesRoots
import ru.art.platform.script.constants.KotlinScriptConstants.KOTLIN_SCRIPT_ADDITIONAL_ARGUMENTS
import ru.art.platform.script.dummy.*
import java.io.*
import java.lang.Thread.*
import java.lang.management.ManagementFactory.*
import kotlin.script.experimental.jvm.util.*


object KotlinScriptEnvironmentManager {
    private val disposable = Disposable { }
    val KOTLIN_SCRIPT_ENVIRONMENT: KotlinCoreEnvironment = createEnvironment()

    private fun createEnvironment(): KotlinCoreEnvironment {
        val arguments = K2JVMCompilerArguments()
        parseCommandLineArguments(KOTLIN_SCRIPT_ADDITIONAL_ARGUMENTS, arguments)
        val configuration = CompilerConfiguration().apply {
            addJvmClasspathRoots(getJdkClassesRoots(File(getRuntimeMXBean().vmVersion)) + currentThread().contextClassLoader.classPathFromTypicalResourceUrls())
            put(DISABLE_PARAM_ASSERTIONS, arguments.noParamAssertions)
            put(DISABLE_CALL_ASSERTIONS, arguments.noCallAssertions)
            put(JDK_HOME, File(getRuntimeMXBean().vmVersion))
            put(MESSAGE_COLLECTOR_KEY, NONE)
            put(MODULE_NAME, KotlinScriptEnvironmentManager.javaClass.simpleName)
            put(TYPED_ARRAYS_ENABLED, true)
            languageVersionSettings = arguments.toLanguageVersionSettings(this[MESSAGE_COLLECTOR_KEY]!!)
        }
        setIdeaIoUseFallback()
        val environment = createForProduction(disposable, configuration, JVM_CONFIG_FILES)
        (environment.project as MockProject).apply {
            registerService(NullableNotNullManager::class.java, object : NullableNotNullManager(this) {
                override fun setInstrumentedNotNulls(p0: MutableList<String>) {}
                override fun getInstrumentedNotNulls(): MutableList<String> {
                    return mutableListOf()
                }

                override fun setNullables(vararg p0: String?) {}
                override fun getDefaultNotNull(): String {
                    return ""
                }

                override fun getNotNulls(): MutableList<String> {
                    return mutableListOf()
                }

                override fun getDefaultNullable(): String {
                    return ""
                }

                override fun setDefaultNotNull(p0: String) {}
                override fun setNotNulls(vararg p0: String?) {}
                override fun getNullables(): MutableList<String> {
                    return mutableListOf()
                }

                override fun setDefaultNullable(p0: String) {}
                override fun isNullable(owner: PsiModifierListOwner, checkBases: Boolean): Boolean {
                    return false
                }

                override fun isNotNull(owner: PsiModifierListOwner, checkBases: Boolean): Boolean {
                    return true
                }
            })
            registerService(CodeStyleManager::class.java, KotlinScriptDummyCodeStyleManager)
        }
        registerExtensionPoints(getRootArea())
        return environment
    }

    private fun registerExtensionPoints(area: ExtensionsArea) {
        registerExtensionPoint(area, BinaryFileStubBuilders.EP_NAME, FileTypeExtensionPoint::class.java)
        registerExtensionPoint(area, FileContextProvider.EP_NAME, FileContextProvider::class.java)
        registerExtensionPoint(area, MetaDataContributor.EP_NAME, MetaDataContributor::class.java)
        registerExtensionPoint(area, PsiAugmentProvider.EP_NAME, PsiAugmentProvider::class.java)
        registerExtensionPoint(area, JavaMainMethodProvider.EP_NAME, JavaMainMethodProvider::class.java)
        registerExtensionPoint(area, ContainerProvider.EP_NAME, ContainerProvider::class.java)
        registerExtensionPoint(area, ClsCustomNavigationPolicy.EP_NAME, ClsCustomNavigationPolicy::class.java)
        registerExtensionPoint<ClassFileDecompilers.Decompiler>(area, ClassFileDecompilers.EP_NAME, ClassFileDecompilers.Decompiler::class.java)
    }
}