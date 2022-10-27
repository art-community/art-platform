import org.gradle.api.JavaVersion.*
import org.jetbrains.kotlin.gradle.tasks.*

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

plugins {
    id("io.github.art.project") version "1.0.113"
}

tasks.withType(Wrapper::class.java) {
    gradleVersion = "7.2"
}

val bintrayUser: String? by project
val bintrayKey: String? by project
val version: String? by project

group = "io.github.art"

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

subprojects {
    repositories {
        jcenter()
        mavenCentral()

    }

    apply(plugin = "io.github.art.project")

    art {
        idea()
        lombok()
        tests()
        dependencyRefreshing {
            refreshingRateInSeconds = 1
        }
        externalDependencyVersions {
            kotlinVersion = "1.4+"
        }
    }

    tasks.withType<KotlinCompile> {
        sourceCompatibility = VERSION_1_8.toString()
        targetCompatibility = VERSION_1_8.toString()

        kotlinOptions {
            jvmTarget = VERSION_1_8.toString()
        }
    }
}

project(":management-panel") {
    (tasks.withType(KotlinCompile::class) + tasks.withType(JavaCompile::class))
            .forEach { task ->
                task.dependsOn(":api:build",
                        ":git-service:build",
                        ":common:build",
                        ":linux-service:build",
                        ":open-shift-service:build",
                        ":docker-service:build")
            }
}

project(":agent") {
    (tasks.withType(KotlinCompile::class) + tasks.withType(JavaCompile::class))
            .forEach { task ->
                task.dependsOn(":api:build",
                        ":common:build",
                        ":git-service:build",
                        ":linux-service:build",
                        ":open-shift-service:build",
                        ":docker-service:build")
            }
}

project(":open-shift-service") {
    (tasks.withType(KotlinCompile::class) + tasks.withType(JavaCompile::class))
            .forEach { task -> task.dependsOn(":api:build") }
}

project(":api") {
    (tasks.withType(KotlinCompile::class) + tasks.withType(JavaCompile::class))
            .forEach { task -> task.dependsOn(":common:build") }
}
