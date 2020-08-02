package ru.art.platform.agent.dsl

import com.google.common.hash.Hashing.crc32
import ru.art.core.colorizer.AnsiColorizer.success
import ru.art.core.constants.StringConstants.SCHEME_DELIMITER
import ru.art.core.constants.StringConstants.SPACE
import ru.art.core.constants.SystemConstants.PROCESS_ERROR_CODE_OK
import ru.art.core.context.Context.contextConfiguration
import ru.art.core.extension.FileExtensions.writeFile
import ru.art.platform.agent.constants.DockerConstants.DOCKER_CONFIG
import ru.art.platform.agent.constants.DockerConstants.DOCKER_CONFIG_NAME
import ru.art.platform.agent.constants.KanikoConstants.KANIKO_CACHE_ARG
import ru.art.platform.agent.constants.KanikoConstants.KANIKO_CACHE_REPO_ARG
import ru.art.platform.agent.constants.KanikoConstants.KANIKO_CONTEXT_ARG
import ru.art.platform.agent.constants.KanikoConstants.KANIKO_DESTINATION_ARG
import ru.art.platform.agent.constants.KanikoConstants.KANIKO_DISABLE_PERMISSIONS_CHANGING_ARG
import ru.art.platform.agent.constants.KanikoConstants.KANIKO_DOCKER_FILE_ARG
import ru.art.platform.agent.constants.KanikoConstants.KANIKO_EXECUTABLE
import ru.art.platform.agent.constants.KanikoConstants.KANIKO_IMAGE_ASSEMBLY_DIRECTORY_ARG
import ru.art.platform.agent.constants.KanikoConstants.KANIKO_INSECURE_ARG
import ru.art.platform.agent.constants.KanikoConstants.KANIKO_INSECURE_PULL_ARG
import ru.art.platform.agent.constants.KanikoConstants.KANIKO_SKIP_TLS_VERIFY_ARG
import ru.art.platform.agent.constants.KanikoConstants.KANIKO_SKIP_TLS_VERIFY_PULL_ARG
import ru.art.platform.agent.constants.KanikoConstants.KANIKO_TEMP_DOCKER_FILE
import ru.art.platform.agent.extension.outputListener
import ru.art.platform.agent.service.AgentKanikoService.executeKaniko
import ru.art.platform.agent.service.KanikoExecutorConfiguration
import ru.art.platform.common.constants.ErrorCodes.KANIKO_BUILD_ERROR
import ru.art.platform.common.constants.PlatformKeywords.CACHE_CAMEL_CASE
import ru.art.platform.common.emitter.Emitter
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.common.extensions.crc32
import ru.art.platform.common.service.ProcessOutputListener
import ru.art.platform.docker.constants.DockerConstants.DOCKERFILE
import java.lang.Runtime.getRuntime
import java.nio.file.Path
import kotlin.system.exitProcess

data class KanikoRepositoryCredentials(val registryUrl: String, val userName: String?, val password: String?)

class KanikoExecutor(private val contextPath: Path) {
    private var destination: String? = null
    private var projectName: String? = null
    private var emitter: Emitter<String>? = null
    private var exitIfError: Boolean = false
    private var cache = false
    private val kanikoCredentials = mutableSetOf<KanikoRepositoryCredentials>()
    private lateinit var cacheRepository: String
    private lateinit var imageAssemblyPath: Path
    private lateinit var imageName: String
    private lateinit var imageVersion: String
    private lateinit var registryUrl: String
    private lateinit var dockerFilePath: Path

    fun imageAssemblyPath(imageAssemblyPath: Path): KanikoExecutor {
        this.imageAssemblyPath = imageAssemblyPath
        return this
    }

    fun cache(repository: String): KanikoExecutor {
        this.cache = true
        this.cacheRepository = repository
        return this
    }

    fun imageName(imageName: String): KanikoExecutor {
        this.imageName = imageName
        return this
    }

    fun imageVersion(imageVersion: String): KanikoExecutor {
        this.imageVersion = imageVersion
        return this
    }

    fun destination(destination: String): KanikoExecutor {
        this.destination = destination
        return this
    }

    fun projectName(projectName: String): KanikoExecutor {
        this.projectName = projectName
        return this
    }

    fun registryUrl(registryUrl: String): KanikoExecutor {
        this.registryUrl = registryUrl
        return this
    }

    fun dockerFilePath(dockerFilePath: Path): KanikoExecutor {
        this.dockerFilePath = dockerFilePath
        return this
    }

    fun exitIfError(toExit: Boolean = true): KanikoExecutor {
        exitIfError = toExit
        return this
    }

    fun emitter(emitter: Emitter<String>): KanikoExecutor {
        this.emitter = emitter
        return this
    }

    fun kanikoCredentials(credentials: Set<KanikoRepositoryCredentials>): KanikoExecutor {
        kanikoCredentials.addAll(credentials)
        return this
    }

    fun execute(then: (String) -> Unit): KanikoExecutor {
        emitter?.emit("Starting Kaniko executor")
        val dockerConfig = DOCKER_CONFIG(kanikoCredentials)
        val dockerConfigPath = "${dockerFilePath.parent.toAbsolutePath()}/${dockerConfig.crc32()}/$DOCKER_CONFIG_NAME"
        writeFile(dockerConfigPath, dockerConfig)
        val command = mutableListOf(KANIKO_EXECUTABLE)
        val destination = destination
                ?: projectName?.let { name -> "${registryUrl.substringAfter(SCHEME_DELIMITER)}/${name}/${imageName}:${imageVersion}" }
                ?: "${registryUrl.substringAfter(SCHEME_DELIMITER)}/${imageName}:${imageVersion}"
        command += KANIKO_DISABLE_PERMISSIONS_CHANGING_ARG
        command += KANIKO_IMAGE_ASSEMBLY_DIRECTORY_ARG
        command += imageAssemblyPath.toAbsolutePath().toString()
        command += KANIKO_SKIP_TLS_VERIFY_ARG
        command += KANIKO_INSECURE_ARG
        command += KANIKO_SKIP_TLS_VERIFY_PULL_ARG
        command += KANIKO_INSECURE_PULL_ARG
        command += KANIKO_DOCKER_FILE_ARG
        command += dockerFilePath.toAbsolutePath().toString()
        command += KANIKO_CONTEXT_ARG
        command += contextPath.toAbsolutePath().toString()
        command += KANIKO_TEMP_DOCKER_FILE
        command += "${imageAssemblyPath.toAbsolutePath()}/$DOCKERFILE"
        command += KANIKO_DESTINATION_ARG
        command += destination
        if (cache) {
            command += KANIKO_CACHE_ARG
            projectName?.let { name ->
                command += KANIKO_CACHE_REPO_ARG
                command += "$cacheRepository/$name/$imageName-$CACHE_CAMEL_CASE"
            }
        }
        emitter?.emit("Kaniko command: ${command.joinToString(SPACE)}")
        emitter?.emit("Kaniko Docker config path: $dockerConfigPath")
        val configuration = KanikoExecutorConfiguration(contextPath = contextPath, command = command, dockerConfigPath = dockerConfigPath)
        val outputListener = emitter?.outputListener() ?: ProcessOutputListener()
        if (executeKaniko(configuration, outputListener).exitValue() == PROCESS_ERROR_CODE_OK) {
            emitter?.emit(success("Kaniko execution completed"))
            then(destination)
            getRuntime().gc()
            return this
        }
        if (!exitIfError) {
            emitter?.emitError(PlatformException(KANIKO_BUILD_ERROR))
            getRuntime().gc()
            return this
        }
        emitter?.completeWithError(PlatformException(KANIKO_BUILD_ERROR))
        exitProcess(PROCESS_ERROR_CODE_OK)
    }
}
