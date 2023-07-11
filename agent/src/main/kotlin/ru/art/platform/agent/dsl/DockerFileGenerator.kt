package ru.art.platform.agent.dsl

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import ru.art.core.colorizer.AnsiColorizer.success
import ru.art.core.constants.StringConstants.EMPTY_STRING
import ru.art.core.extension.FileExtensions.writeFile
import ru.art.platform.agent.template.*
import ru.art.platform.common.constants.JdkVersions.JDK_VERSION_11
import ru.art.platform.common.constants.JdkVersions.JDK_VERSION_1_8
import ru.art.platform.common.emitter.Emitter
import ru.art.platform.docker.constants.DockerConstants.DOCKERFILE
import ru.art.platform.docker.constants.DockerConstants.LOCAL_PATHS
import ru.art.platform.docker.constants.DockerConstants.JAR_NAME
import ru.art.platform.docker.constants.DockerConstants.JAR_VERSION
import ru.art.platform.docker.constants.DockerConstants.JDK_11_IMAGE_VERSION
import ru.art.platform.docker.constants.DockerConstants.JDK_8_IMAGE_VERSION
import ru.art.platform.docker.constants.DockerConstants.JDK_IMAGE
import ru.art.platform.docker.constants.DockerConstants.JVM_OPTIONS
import ru.art.platform.docker.constants.DockerConstants.LAUNCHER_FILE_NAME
import ru.art.platform.docker.constants.DockerConstants.LAUNCHER_SH
import ru.art.platform.docker.constants.DockerConstants.NGINX_CONFIGURATION_FILE_NAME
import ru.art.platform.docker.constants.DockerConstants.NGINX_IMAGE
import ru.art.platform.docker.constants.DockerConstants.NGINX_LOCAL_STATIC_PATHS
import ru.art.platform.docker.constants.DockerConstants.WORKING_DIRECTORY
import ru.art.platform.linux.constants.LinuxConstants.SH
import java.io.StringWriter
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.collections.set

class DockerJarConfiguration {
    var jvmOptions: String = EMPTY_STRING
    lateinit var jdkImage: String
    lateinit var localPaths: Set<String>
    lateinit var name: String
    lateinit var version: String
    lateinit var workingDirectory: String
    var directories: MutableMap<String, String> = mutableMapOf()

    fun jdkImage(jdkImage: String): DockerJarConfiguration {
        this.jdkImage = jdkImage
        return this
    }

    fun jvmOptions(jvmOptions: String): DockerJarConfiguration {
        this.jvmOptions = jvmOptions
        return this
    }

    fun jdkImage(image: String, version: String): DockerJarConfiguration {
        when (version) {
            JDK_VERSION_1_8 -> jdkImage("$image:$JDK_8_IMAGE_VERSION")
            JDK_VERSION_11 -> jdkImage("$image:$JDK_11_IMAGE_VERSION")
            else -> "$image/$JDK_8_IMAGE_VERSION"
        }
        return this
    }

    fun workingDirectory(workingDirectory: String): DockerJarConfiguration {
        this.workingDirectory = workingDirectory
        return this
    }

    fun localPaths(localPaths: Set<String>): DockerJarConfiguration {
        this.localPaths = localPaths
        return this
    }

    fun name(name: String): DockerJarConfiguration {
        this.name = name
        return this
    }

    fun version(version: String): DockerJarConfiguration {
        this.version = version
        return this
    }

    fun directories(directories: Map<String, String>): DockerJarConfiguration {
        this.directories = directories.toMutableMap()
        return this
    }

    fun copy(from: String, to: String): DockerJarConfiguration {
        this.directories[from] = to;
        return this
    }
}

class DockerNginxConfiguration {
    var staticContentPaths: MutableSet<String> = mutableSetOf()
    lateinit var nginxImage: String
    lateinit var workingDirectory: String
    lateinit var name: String
    lateinit var version: String

    fun nginxImage(nginxImage: String): DockerNginxConfiguration {
        this.nginxImage = nginxImage
        return this
    }

    fun workingDirectory(workingDirectory: String): DockerNginxConfiguration {
        this.workingDirectory = workingDirectory
        return this
    }

    fun staticContentPaths(paths: Set<String>): DockerNginxConfiguration {
        this.staticContentPaths = paths.toMutableSet()
        return this
    }

    fun staticContent(path: String): DockerNginxConfiguration {
        this.staticContentPaths.add(path);
        return this
    }


    fun name(name: String): DockerNginxConfiguration {
        this.name = name
        return this
    }

    fun version(version: String): DockerNginxConfiguration {
        this.version = version
        return this
    }
}

class DockerFileGenerator(private val directory: Path) {
    private var emitter: Emitter<String>? = null
    private var fileTemplates = mutableMapOf<String, FileTemplate>()

    fun jarImage(configurator: DockerJarConfiguration.() -> DockerJarConfiguration): DockerFileGenerator {
        val configuration = configurator(DockerJarConfiguration())

        fileTemplates[DOCKERFILE] = FileTemplate(
                path = Paths.get(DOCKERFILE),
                content = DOCKER_JAR_TEMPLATE,
                context = mapOf(
                        WORKING_DIRECTORY to configuration.workingDirectory,
                        LAUNCHER_FILE_NAME to LAUNCHER_SH,
                        JDK_IMAGE to configuration.jdkImage,
                        LOCAL_PATHS to configuration.localPaths
                )
        )

        fileTemplates[LAUNCHER_SH] = FileTemplate(
                path = Paths.get(LAUNCHER_SH),
                content = DOCKER_JAR_LAUNCHER_TEMPLATE,
                context = mapOf(
                        JAR_NAME to configuration.name,
                        JAR_VERSION to configuration.version,
                        JVM_OPTIONS to configuration.jvmOptions
                )
        )

        return this
    }

    fun nginxImage(configurator: DockerNginxConfiguration.() -> DockerNginxConfiguration): DockerFileGenerator {
        val configuration = configurator(DockerNginxConfiguration())

        fileTemplates[DOCKERFILE] = FileTemplate(
                path = Paths.get(DOCKERFILE),
                content = DOCKER_NGINX_TEMPLATE,
                context = mapOf(
                        WORKING_DIRECTORY to configuration.workingDirectory,
                        NGINX_IMAGE to configuration.nginxImage,
                        NGINX_LOCAL_STATIC_PATHS to configuration.staticContentPaths
                )
        )

        fileTemplates[NGINX_CONFIGURATION_FILE_NAME] = FileTemplate(
                path = Paths.get(NGINX_CONFIGURATION_FILE_NAME),
                content = DOCKER_NGINX_CONFIGURATION_TEMPLATE,
                context = mapOf(
                        WORKING_DIRECTORY to configuration.workingDirectory
                )
        )

        return this
    }

    fun emitter(emitter: Emitter<String>): DockerFileGenerator {
        this.emitter = emitter
        return this
    }

    fun generate(handler: Path.() -> Unit = {}): DockerFileGenerator {
        fileTemplates.forEach { (name, template) ->
            val writer = StringWriter()
            PebbleEngine.Builder()
                    .loader(ClasspathLoader())
                    .autoEscaping(false)
                    .cacheActive(false)
                    .build()
                    .getLiteralTemplate(template.content)
                    .evaluate(writer, template.context)
            val content = writer.toString()
            val path = directory.resolve(name)
            writeFile(path, content)
            if (name.endsWith(SH)) {
                path.toFile().setExecutable(true)
            }
            emitter?.emit(success("Generated file: ${path.toAbsolutePath()}"))
            emitter?.emit(content)
        }

        handler(directory.resolve(DOCKERFILE))
        return this
    }
}
