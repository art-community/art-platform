package ru.art.platform.agent.dsl

import ru.art.core.colorizer.AnsiColorizer.success
import ru.art.core.extension.FileExtensions.readFile
import ru.art.platform.agent.dsl.AgentDsl.kaniko
import ru.art.platform.common.emitter.Emitter
import ru.art.platform.open.shift.constants.OpenShiftConstants.SERVICE_ACCOUNT
import ru.art.platform.open.shift.constants.OpenShiftConstants.SERVICE_ACCOUNT_TOKEN_PATH
import java.nio.file.Path

class DockerImageUploader {
    private lateinit var registryUrl: String
    private lateinit var imageName: String
    private lateinit var imageVersion: String
    private lateinit var imageWorkingDirectory: Path
    private lateinit var imageContextPath: Path
    private lateinit var projectName: String
    private lateinit var dockerFilePath: Path
    private val kanikoCredentials = mutableSetOf<KanikoRepositoryCredentials>()
    private var emitter: Emitter<String>? = null
    private var exitIfError: Boolean = false
    private var cache = false;

    fun pushToRegistry(url: String, userName: String?, password: String?): DockerImageUploader {
        credentials(KanikoRepositoryCredentials(url, userName, password))
        this.registryUrl = url
        return this
    }

    fun pushToLocalOpenShiftRegistry(url: String): DockerImageUploader {
        pushToRegistry(url, SERVICE_ACCOUNT, readFile(SERVICE_ACCOUNT_TOKEN_PATH))
        return this
    }

    fun useRegistry(url: String, userName: String?, password: String?): DockerImageUploader {
        credentials(KanikoRepositoryCredentials(url, userName, password))
        return this
    }

    fun useLocalOpenShiftRegistry(url: String): DockerImageUploader {
        credentials(KanikoRepositoryCredentials(url, SERVICE_ACCOUNT, readFile(SERVICE_ACCOUNT_TOKEN_PATH)))
        return this
    }

    fun projectName(projectName: String): DockerImageUploader {
        this.projectName = projectName
        return this
    }


    fun imageName(imageName: String): DockerImageUploader {
        this.imageName = imageName
        return this
    }

    fun imageWorkingDirectory(imageWorkingDirectory: Path): DockerImageUploader {
        this.imageWorkingDirectory = imageWorkingDirectory
        return this
    }

    fun imageContextPath(imageContextPath: Path): DockerImageUploader {
        this.imageContextPath = imageContextPath
        return this
    }

    fun credentials(credentials: KanikoRepositoryCredentials): DockerImageUploader {
        this.kanikoCredentials.add(credentials)
        return this
    }

    fun imageVersion(version: String): DockerImageUploader {
        this.imageVersion = version
        return this
    }

    fun dockerFile(dockerFilePath: Path): DockerImageUploader {
        this.dockerFilePath = dockerFilePath
        return this
    }

    fun dockerFile(dockerFileDirectoryPath: Path, generator: DockerFileGenerator.() -> DockerFileGenerator): DockerImageUploader {
        generator(DockerFileGenerator(dockerFileDirectoryPath)).apply { emitter?.let(::emitter) }.generate { dockerFilePath = this }
        return this
    }

    fun emitter(emitter: Emitter<String>): DockerImageUploader {
        this.emitter = emitter
        return this
    }

    fun exitIfError(toExit: Boolean = true): DockerImageUploader {
        exitIfError = toExit
        return this
    }

    fun cache(cache: Boolean = true): DockerImageUploader {
        this.cache = cache
        return this
    }

    fun upload(then: String.() -> Unit): DockerImageUploader {
        emitter?.emit("[$projectName/$imageName:$imageVersion]: Staring upload to Docker registry $registryUrl")
        kaniko(imageContextPath) {
            if (cache) {
                cache(registryUrl)
            }
            registryUrl(registryUrl)
            projectName(projectName)
            imageName(imageName)
            imageVersion(imageVersion)
            imageAssemblyPath(imageWorkingDirectory)
            exitIfError(exitIfError)
            dockerFilePath(dockerFilePath)
            kanikoCredentials(kanikoCredentials)
            emitter?.let(::emitter)

            execute { remotePath ->
                emitter?.emit(success("[$projectName:$imageName-$imageVersion]: Upload finished. The remote path: $remotePath"))
                then(remotePath)
            }
        }
        return this
    }
}
