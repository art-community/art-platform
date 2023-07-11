package ru.art.platform.docker.constants

object DockerConstants {
    const val DOCKERFILE = "Dockerfile"
    const val LAUNCHER_SH = "launcher.sh"

    const val JDK_IMAGE = "jdkImage"
    const val LOCAL_PATHS = "localPaths"
    const val JAR_NAME = "jarName"
    const val JAR_VERSION = "jarVersion"

    const val JVM_OPTIONS = "jvmOptions"

    const val WORKING_DIRECTORY = "workingDirectory"
    const val LAUNCHER_FILE_NAME = "launcherFileName"
    const val DOCKER = "docker"
    const val SERVICE = "service"
    const val START = "start"
    const val LOGIN = "login"
    const val BUILD = "build"
    const val TAG = "tag"
    const val PUSH = "push"
    const val TAG_OPTION = "-t"
    const val REMOVE_OPTION = "--rm"
    const val USER_NAME_OPTION = "-u"
    const val PASSWORD_OPTION = "-p"

    const val JDK_8_IMAGE_VERSION = "8"
    const val JDK_11_IMAGE_VERSION = "11"

    const val NGINX_IMAGE = "nginxImage"
    const val NGINX_CONFIGURATION_FILE_NAME = "nginx.conf"
    const val NGINX_CONTAINER_STATIC_FILES_DIRECTORY_NAME = "static"
    const val NGINX_LOCAL_STATIC_PATHS = "localPaths"
}
