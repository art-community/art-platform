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
        kit()
    }
    java {
        jar("agent.jar")
    }
    mainClass("ru.art.platform.agent.module.AgentModule")
}


dependencies {
    embedded(project(":api"))
    embedded(project(":common"))
    embedded(project(":open-shift-service"))
    embedded(project(":docker-service"))
    embedded(project(":git-service"))
    embedded(project(":linux-service"))
    embedded("org.zeroturnaround", "zt-exec", art.externalDependencyVersionsConfiguration.zeroTurnaroundVersion)
    embedded("org.zeroturnaround", "zt-zip", art.externalDependencyVersionsConfiguration.zeroTurnaroundVersion)
    embedded("javax.mail", "javax.mail-api", "1.6+")
    embedded("com.sun.mail", "javax.mail", "1+")
}

task("buildDockerImage", type = Exec::class) {
    group = "docker"
    dependsOn += "build"
    if (os.current().isWindows) {
        commandLine("cmd", "/c", "docker build --rm -t platform/agent:$version .")
        return@task
    }
    commandLine("docker", "build", "--rm", "-t", "platform/agent:$version", ".")
}


dockerProjects.let { projects ->
    projects.split(",").forEach { dockerProject ->
        task("$dockerProject-tag", type = Exec::class) {
            group = "docker"
            dependsOn += "buildDockerImage"
            if (os.current().isWindows) {
                commandLine("cmd", "/c", "docker tag platform/agent:$version $dockerRegistryUrl/$dockerProject/agent:$version")
                return@task
            }
            commandLine("docker", "tag", "platform/agent:$version", "$dockerRegistryUrl/$dockerProject/agent:$version")
        }
        task("$dockerProject-push", type = Exec::class) {
            group = "docker"
            dependsOn += "$dockerProject-tag"
            if (os.current().isWindows) {
                commandLine("cmd", "/c", "docker push $dockerRegistryUrl/$dockerProject/agent:$version")
                return@task
            }
            commandLine("docker", "push", "$dockerRegistryUrl/$dockerProject/agent")
        }
    }
}
