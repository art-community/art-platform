package ru.art.platform.agent.dsl

import ru.art.core.constants.StringConstants.*
import ru.art.core.constants.SystemConstants.PROCESS_ERROR_CODE_OK
import ru.art.core.extension.FileExtensions.writeFile
import ru.art.http.constants.HttpCommonConstants.HTTP_SCHEME
import ru.art.platform.agent.constants.GradleConstants.GRADLE_BUILD_CACHE
import ru.art.platform.agent.constants.GradleConstants.GRADLE_EXECUTABLE_5
import ru.art.platform.agent.constants.GradleConstants.GRADLE_EXECUTABLE_6
import ru.art.platform.agent.constants.GradleConstants.GRADLE_GROOVY_SCRIPT_FORMAT
import ru.art.platform.agent.constants.GradleConstants.GRADLE_INIT_SCRIPT_FLAG
import ru.art.platform.agent.constants.GradleConstants.GRADLE_INIT_SCRIPT_GROOVY_FILE
import ru.art.platform.agent.constants.GradleConstants.GRADLE_INIT_SCRIPT_KTS_FILE
import ru.art.platform.agent.constants.GradleConstants.GRADLE_JDK_11_OPTION
import ru.art.platform.agent.constants.GradleConstants.GRADLE_JDK_1_8_OPTION
import ru.art.platform.agent.constants.GradleConstants.GRADLE_KTS_SCRIPT_FORMAT
import ru.art.platform.agent.constants.GradleConstants.GRADLE_PROPERTY_FLAG
import ru.art.platform.agent.extension.outputListener
import ru.art.platform.agent.service.AgentGradleService.executeGradleBuild
import ru.art.platform.agent.service.AgentGradleService.findGradleModules
import ru.art.platform.agent.service.GradleBuildConfiguration
import ru.art.platform.api.model.assembly.Assembly
import ru.art.platform.api.model.assembly.AssemblyCacheConfiguration
import ru.art.platform.api.model.assembly.AssemblyConfiguration
import ru.art.platform.api.model.gradle.GradleArtifactConfiguration
import ru.art.platform.api.model.gradle.GradleAssemblyConfiguration
import ru.art.platform.common.constants.ErrorCodes.GRADLE_BUILD_ERROR
import ru.art.platform.common.constants.GradleVersions.GRADLE_VERSION_5_6_1
import ru.art.platform.common.constants.GradleVersions.GRADLE_VERSION_6_0_1
import ru.art.platform.common.constants.JdkVersions.JDK_VERSION_11
import ru.art.platform.common.constants.JdkVersions.JDK_VERSION_1_8
import ru.art.platform.common.constants.PlatformKeywords.CACHE_CAMEL_CASE
import ru.art.platform.common.constants.PropertyTypes.*
import ru.art.platform.common.emitter.Emitter
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.common.service.ProcessOutputListener
import java.lang.Runtime.getRuntime
import java.nio.file.Path
import kotlin.system.exitProcess

data class GradleArtifact(val name: String, val version: String, val localPath: Path)

class GradleBuilder(private val projectPath: Path) {
    private var clean = false
    private var properties = mutableMapOf<String, String>()
    private var initScriptPath: Path? = null
    private var emitter: Emitter<String>? = null
    private var exitIfError = false
    private var arguments = mutableSetOf<String>()
    private var javaHomeOption: String = GRADLE_JDK_1_8_OPTION
    private var gradleExecutable = GRADLE_EXECUTABLE_6
    private var artifacts = mutableMapOf<String, GradleArtifactConfiguration>()
    private var cache = false

    fun configure(gradleConfiguration: GradleAssemblyConfiguration): GradleBuilder {
        with(gradleConfiguration) {
            jdkVersion(gradleConfiguration.jdkVersion ?: JDK_VERSION_1_8)
            gradleVersion(version ?: GRADLE_VERSION_5_6_1)
            initScriptFormat?.let { format ->
                when (format) {
                    GRADLE_KTS_SCRIPT_FORMAT -> initScriptKotlinContent?.let { content -> initScript(format, content) }
                    GRADLE_GROOVY_SCRIPT_FORMAT -> initScriptGroovyContent?.let { content -> initScript(format, content) }
                    else -> {
                    }
                }
            }
            arguments?.let { arguments -> arguments(arguments.split(SPACE)) }
            properties?.forEach { property ->
                when (property.type) {
                    TEXT_PROPERTY -> property(property.name, property.textProperty.value)
                    RESOURCE_PROPERTY -> property(property.name, property.resourceProperty.value)
                }
            }
        }
        return this
    }

    fun configure(assembly: Assembly, assemblyConfiguration: AssemblyConfiguration, assemblyCacheConfiguration: AssemblyCacheConfiguration): GradleBuilder {
        configure(assemblyConfiguration.gradleConfiguration)
        assemblyConfiguration.gradleConfiguration?.apply {
            properties?.forEach { property ->
                when (property.type) {
                    VERSION_PROPERTY -> property(property.name, assembly.version.version)
                }
            }
            cacheConfiguration?.let { cache ->
                if (!cache.serverUrlProperty.isNullOrBlank()) {
                    cache()
                    val port = "$HTTP_SCHEME$SCHEME_DELIMITER${assemblyCacheConfiguration.serverHost}:${assemblyCacheConfiguration.serverPort}/$CACHE_CAMEL_CASE/"
                    property(cache.serverUrlProperty, port)
                }
            }
        }
        return this
    }

    fun cache(cache: Boolean = true) {
        this.cache = cache
    }

    fun gradleVersion(version: String): GradleBuilder {
        if (version.isEmpty()) {
            return this
        }
        when (version) {
            GRADLE_VERSION_6_0_1 -> gradleExecutable = GRADLE_EXECUTABLE_6
            GRADLE_VERSION_5_6_1 -> gradleExecutable = GRADLE_EXECUTABLE_5
        }
        return this
    }

    fun clean(toClean: Boolean = true): GradleBuilder {
        this.clean = toClean
        return this
    }

    fun argument(argument: String) {
        this.arguments.add(argument)
    }

    fun arguments(arguments: Collection<String>) {
        this.arguments = arguments.toMutableSet()
    }

    fun addArguments(arguments: Collection<String>) {
        this.arguments.addAll(arguments)
    }

    fun artifact(name: String, configuration: GradleArtifactConfiguration) {
        this.artifacts[name] = configuration;
    }

    fun artifacts(artifacts: Map<String, GradleArtifactConfiguration>) {
        this.artifacts = artifacts.toMutableMap()
    }

    fun javaHome(path: String): GradleBuilder {
        this.javaHomeOption = path
        return this
    }

    fun jdkVersion(version: String): GradleBuilder {
        if (version.isEmpty()) {
            return this
        }
        when (version) {
            JDK_VERSION_1_8 -> javaHomeOption = GRADLE_JDK_1_8_OPTION
            JDK_VERSION_11 -> javaHomeOption = GRADLE_JDK_11_OPTION
        }
        return this
    }

    fun property(name: String, value: String): GradleBuilder {
        properties[name] = value
        return this
    }

    fun properties(properties: Map<String, String>): GradleBuilder {
        this.properties = properties.toMutableMap()
        return this
    }

    fun addProperties(properties: Map<String, String>): GradleBuilder {
        this.properties.putAll(properties)
        return this
    }

    fun initScript(format: String, content: String): GradleBuilder {
        if (content.isEmpty() || format.isEmpty()) {
            return this
        }
        initScriptPath = when (format) {
            GRADLE_KTS_SCRIPT_FORMAT -> projectPath.resolve(GRADLE_INIT_SCRIPT_KTS_FILE).also { filePath -> writeFile(filePath, content) }
            GRADLE_GROOVY_SCRIPT_FORMAT -> projectPath.resolve(GRADLE_INIT_SCRIPT_GROOVY_FILE).also { filePath -> writeFile(filePath, content) }
            else -> throw PlatformException(GRADLE_BUILD_ERROR, "Unknown init script format: $format")
        }
        return this
    }

    fun emitter(emitter: Emitter<String>): GradleBuilder {
        this.emitter = emitter
        return this
    }

    fun exitIfError(toExit: Boolean = true): GradleBuilder {
        exitIfError = toExit
        return this
    }

    fun build(then: GradleBuildResult.() -> Unit): GradleBuilder {
        emitter?.emit("Starting Gradle build")
        val command = mutableListOf(gradleExecutable)
        command.addAll(arguments)

        artifacts.forEach { (name, configuration) ->
            configuration.arguments.split(SPACE).forEach { argument ->
                command.add(":$name$argument")
            }
        }

        if (cache) {
            command.add(GRADLE_BUILD_CACHE)
        }

        command.add(javaHomeOption)
        initScriptPath?.let { path ->
            command.apply {
                add(GRADLE_INIT_SCRIPT_FLAG)
                add(path.toAbsolutePath().toString())
            }
        }
        emitter?.emit("Gradle command without properties: ${command.joinToString(SPACE)}")
        properties.forEach { (name, value) -> command.add("$GRADLE_PROPERTY_FLAG$name$EQUAL$value") }
        val configuration = GradleBuildConfiguration(projectPath = projectPath, executable = gradleExecutable, command = command)
        val outputListener = emitter?.outputListener() ?: ProcessOutputListener()
        val result = GradleBuildResult(projectPath = projectPath)
        if (executeGradleBuild(configuration, outputListener).exitValue() == PROCESS_ERROR_CODE_OK) {
            emitter?.emit("Gradle build completed")
            getRuntime().gc()
            then(result)
            return this
        }
        if (!exitIfError) {
            emitter?.emitError(PlatformException(GRADLE_BUILD_ERROR))
            getRuntime().gc()
            return this
        }
        emitter?.completeWithError(PlatformException(GRADLE_BUILD_ERROR))
        exitProcess(PROCESS_ERROR_CODE_OK)
    }
}

class GradleBuildResult(private val projectPath: Path) {
    fun modules(): Map<String, Path> = findGradleModules(projectPath)
            .map { module -> module.name to module.path }
            .toMap()

    fun artifacts(version: String): List<GradleArtifact> = modules()
            .map { gradleModule -> GradleArtifact(gradleModule.key, version, gradleModule.value) }

}
