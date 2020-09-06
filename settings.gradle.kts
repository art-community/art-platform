plugins {
    id("com.gradle.enterprise") version "3.0"
}

rootProject.name = "art-platform"
include("common")
include("api")
include("linux-service")
include("git-service")
include("open-shift-service")
include("docker-service")
include("panel")
include("agent")
include("kotlin-script-service")
