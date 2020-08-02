package ru.art.platform.script.constants

import org.jetbrains.kotlin.idea.util.IdeDescriptorRenderers.APPROXIMATE_FLEXIBLE_TYPES
import org.jetbrains.kotlin.idea.util.IdeDescriptorRenderers.SOURCE_CODE
import org.jetbrains.kotlin.renderer.ClassifierNamePolicy.*
import org.jetbrains.kotlin.renderer.ParameterNameRenderingPolicy.*
import org.jetbrains.kotlin.types.*

object KotlinScriptConstants {
    const val KOTLIN_DEFAULT_SCRIPT_NAME = "DefaultScript"
    val KOTLIN_SCRIPT_COMPILER_RENDERER = SOURCE_CODE.withOptions {
        classifierNamePolicy = SHORT
        typeNormalizer = APPROXIMATE_FLEXIBLE_TYPES
        parameterNameRenderingPolicy = NONE
        typeNormalizer = { kotlinType: KotlinType ->
            when {
                kotlinType.isFlexible() -> {
                    kotlinType.asFlexibleType().upperBound
                }
                else -> kotlinType
            }
        }
    }
    const val KOTLIN_SCRIPT_COMPLETION_DELIMITER = "KOTLIN_SCRIPT_COMPLETION_DELIMITER"
    const val KOTLIN_SCRIPT_NUMBER_OF_CHAR_IN_COMPLETION_NAME = 40
    const val KOTLIN_SCRIPT_NUMBER_OF_CHAR_IN_TAIL = 60
    const val DOT_KT = ".kt"
    const val KT = "Kt"
    const val KT_DOT_CLASS = "$KT.class"
    const val RUN = "run"
    const val CALL = "call"
    val KOTLIN_SCRIPT_ADDITIONAL_ARGUMENTS: List<String> = listOf(
            "-Xuse-experimental=kotlin.Experimental",
            "-Xuse-experimental=kotlin.ExperimentalStdlibApi",
            "-Xuse-experimental=kotlin.time.ExperimentalTime",
            "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes",
            "-Xuse-experimental=kotlin.contracts.ExperimentalContracts",
            "-Xuse-experimental=kotlin.experimental.ExperimentalTypeInference",
            "-XXLanguage:+InlineClasses"
    )
    const val KOTLIN_SCRIPT_RED_WAVY_LINE = "red_wavy_line"
    const val KOTLIN_SCRIPT_ERROR_MESSAGE = "This cast can never succeed"
    const val METHOD = "method"
    const val CLASS = "class"
    const val PACKAGE = "package"
    const val GENERIC_VALUE = "genericValue"
    const val IMPORT = "import "
}