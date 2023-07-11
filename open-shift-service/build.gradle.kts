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

art {
    providedModules {
        kit()
    }
}

dependencies {
    provided(project(":api"))
    provided(project(":common"))
    embedded("com.openshift", "openshift-restclient-java", "+")
            .exclude("commons-logging")
            .exclude("commons-codec")
            .exclude("io.netty")
            .exclude("com.fasterxml.jackson.dataformat")
            .exclude("com.fasterxml.jackson.core")
            .exclude("com.google.guava")
            .exclude("org.apache.httpcomponents")
            .exclude("org.slf4j")
            .exclude("com.squareup.okio")
}
