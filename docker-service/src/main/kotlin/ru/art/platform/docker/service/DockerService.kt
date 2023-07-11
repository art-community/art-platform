package ru.art.platform.docker.service

import ru.art.core.constants.StringConstants.*
import ru.art.platform.common.service.*
import ru.art.platform.docker.constants.DockerConstants.BUILD
import ru.art.platform.docker.constants.DockerConstants.DOCKER
import ru.art.platform.docker.constants.DockerConstants.LOGIN
import ru.art.platform.docker.constants.DockerConstants.PASSWORD_OPTION
import ru.art.platform.docker.constants.DockerConstants.PUSH
import ru.art.platform.docker.constants.DockerConstants.REMOVE_OPTION
import ru.art.platform.docker.constants.DockerConstants.SERVICE
import ru.art.platform.docker.constants.DockerConstants.START
import ru.art.platform.docker.constants.DockerConstants.TAG
import ru.art.platform.docker.constants.DockerConstants.TAG_OPTION
import ru.art.platform.docker.constants.DockerConstants.USER_NAME_OPTION
import java.nio.file.*
import java.nio.file.Paths.*

fun docker(directory: String) = docker(get(directory))

fun docker(directory: Path = get(EMPTY_STRING)) = DockerService(directory)

class DockerService(private val directory: Path) {
    fun startService(): DockerService {
        process(listOf(SERVICE, DOCKER, START), directory).execute()
        return this
    }

    fun login(url: String): DockerService {
        process(listOf(DOCKER, LOGIN, url), directory).execute()
        return this
    }

    fun login(userName: String, password: String, url: String): DockerService {
        process(listOf(DOCKER, LOGIN, USER_NAME_OPTION, userName, PASSWORD_OPTION, password, url), directory).execute()
        return this
    }

    fun buildImage(tag: String): DockerService {
        process(listOf(DOCKER, BUILD, REMOVE_OPTION, TAG_OPTION, tag, DOT), directory).execute()
        return this
    }

    fun tagImage(source: String, target: String): DockerService {
        process(listOf(DOCKER, TAG, source, target), directory).execute()
        return this
    }

    fun pushImage(tag: String): DockerService {
        process(listOf(DOCKER, PUSH, tag), directory).execute()
        return this
    }
}