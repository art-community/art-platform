art {
    providedModules {
        kit()
    }
    generator {
        packageName = "ru.art.platform.api"
    }
}

dependencies {
    provided(project(":common"))
}