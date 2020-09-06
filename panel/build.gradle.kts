/*
 * ART Java
 *
 * Copyright 2019 ART
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gradle.internal.os.OperatingSystem as os

val dockerRegistryUrl: String by project
val dockerProjects: String by project

art {
    embeddedModules {
        useVersion("1.2.8")
        kit()
    }
    java {
        jar("panel.jar")
    }
    web {
        buildToolCheckingCommand =
                if (os.current().isWindows)
                    arrayOf("cmd", "/c", "yarn", "--no-lockfile", "--skip-integrity-check")
                else
                    arrayOf("yarn", "--no-lockfile", "--skip-integrity-check")
        buildWebCommand = if (os.current().isWindows) listOf("cmd", "/c", "yarn", "run", "production") else listOf("yarn", "run", "production")
        prepareWebCommand = if (os.current().isWindows) listOf("cmd", "/c", "yarn") else listOf("yarn")
    }
    mainClass("ru.art.platform.panel.module.PanelModule")
    spockFramework()
}

dependencies {
    embedded(project(":api"))
    embedded(project(":open-shift-service"))
    embedded(project(":git-service"))
    embedded(project(":common"))

    embedded("com.auth0", "java-jwt", "3.8.+")
            .exclude("com.fasterxml.jackson.dataformat")
            .exclude("com.fasterxml.jackson.core")
            .exclude("com.google.guava")
            .exclude("org.slf4j")
    embedded("com.google.crypto.tink", "tink", "+")
            .exclude("com.fasterxml.jackson.dataformat")
            .exclude("com.fasterxml.jackson.core")
            .exclude("com.google.guava")
            .exclude("org.slf4j")
    embedded("io.prometheus", "simpleclient", "0.8.1")
            .exclude("com.fasterxml.jackson.dataformat")
            .exclude("com.fasterxml.jackson.core")
            .exclude("com.google.guava")
            .exclude("org.slf4j")
    embedded("io.prometheus", "simpleclient_hotspot", "0.8.1")
            .exclude("com.fasterxml.jackson.dataformat")
            .exclude("com.fasterxml.jackson.core")
            .exclude("com.google.guava")
            .exclude("org.slf4j")
    embedded(files("libraries/rsocket-transport-netty-1.0.1.jar"))
}

task("buildDockerImage", type = Exec::class) {
    group = "docker"
    dependsOn += "build"
    if (os.current().isWindows) {
        commandLine("cmd", "/c", "docker build --rm -t platform/panel:$version .")
        return@task
    }
    commandLine("docker", "build", "--rm", "-t", "platform/panel:$version", ".")
}

dockerProjects.let { projects ->
    projects.split(",").forEach { dockerProject ->
        task("$dockerProject-tag", type = Exec::class) {
            group = "docker"
            dependsOn += "buildDockerImage"
            if (os.current().isWindows) {
                commandLine("cmd", "/c", "docker tag platform/panel:$version $dockerRegistryUrl/$dockerProject/panel:$version")
                return@task
            }
            commandLine("docker", "tag", "platform/panel:$version", "$dockerRegistryUrl/$dockerProject/panel:$version")
        }

        task("$dockerProject-push", type = Exec::class) {
            group = "docker"
            dependsOn += "$dockerProject-tag"
            if (os.current().isWindows) {
                commandLine("cmd", "/c", "docker push $dockerRegistryUrl/$dockerProject/panel:$version")
                return@task
            }
            commandLine("docker", "push", "$dockerRegistryUrl/$dockerProject/panel")
        }
    }
}
