import gradle from "../images/gradle.png";
import npm from "../images/npm.jpg";
import yarn from "../images/yarn.jpg";
import docker from "../images/docker.png";
import gatling from "../images/gatling.png";
import maven from "../images/maven.png";
import js from "../images/js.png";
import java from "../images/java.webp";
import kotlin from "../images/kotlin.png";
import groovy from "../images/groovy.png";
import typescript from "../images/typescript.svg";
import envoy from "../images/envoy.png";
import nginx from "../images/nginx.png";
import caddy from "../images/caddy.jpg";
import {ResourceIdentifier} from "../model/ResourceTypes";
import {ARTIFACTS_RESOURCE, OPEN_SHIFT_RESOURCE} from "./ResourceConstants";

export const GRADLE = 'gradle';
export const GATLING = 'gatling';
export const NPM = 'npm';
export const YARN = 'yarn';
export const DOCKER = 'docker';
export const MAVEN = 'maven';
export const JAVA = 'java';
export const JAR = 'JAR';
export const JVM = 'jvm';
export const JS = 'js';
export const KOTLIN = 'kotlin';
export const GROOVY = 'groovy';
export const TS = 'ts';
export const ENVOY = 'envoy';
export const NGINX = 'nginx';
export const CADDY = 'caddy';

export interface Technology {
    name: string
    descriptionReference: string
    descriptionLabel: string
    description: string
    icon: string
    width: number
    height: number
}

export const TECHNOLOGIES: Technology[] = [];

TECHNOLOGIES.push({
    name: GATLING,
    descriptionReference: "https://gatling.io/",
    descriptionLabel: "Gatling",
    description: "используется для проведения нагрузочного тестирования",
    icon: gatling,
    width: 50,
    height: 50
});

TECHNOLOGIES.push({
    name: GRADLE,
    descriptionReference: "https://gradle.org/",
    descriptionLabel: "Gradle",
    description: "используется для сборки JVM-based и C++ проектов",
    icon: gradle,
    width: 50,
    height: 50
});

TECHNOLOGIES.push({
    name: NPM,
    descriptionReference: "https://www.npmjs.com/",
    descriptionLabel: "NPM",
    description: "используется для сборки JS проектов",
    icon: npm,
    width: 50,
    height: 50
});

TECHNOLOGIES.push({
    name: YARN,
    descriptionReference: "https://yarnpkg.com/",
    descriptionLabel: "YARN",
    description: "используется для сборки JS проектов",
    icon: yarn,
    width: 50,
    height: 50
});

TECHNOLOGIES.push({
    name: DOCKER,
    descriptionReference: "https://www.docker.com/",
    descriptionLabel: "Docker",
    description: "используется для создания и выполнения контейнеров",
    icon: docker,
    width: 50,
    height: 50
});

TECHNOLOGIES.push({
    name: MAVEN,
    descriptionReference: "https://maven.apache.org/",
    descriptionLabel: "Maven",
    description: "используется для сборки Java проектов",
    icon: maven,
    width: 50,
    height: 50
});

TECHNOLOGIES.push({
    name: JAVA,
    descriptionReference: "https://www.java.com/",
    descriptionLabel: "Java",
    description: "язык программирования (используется для backend разработки)",
    icon: java,
    width: 50,
    height: 50
});

TECHNOLOGIES.push({
    name: JAR,
    descriptionReference: "https://www.java.com/",
    descriptionLabel: "JAR",
    description: "исполняемый архив для JVM",
    icon: java,
    width: 50,
    height: 50
});

TECHNOLOGIES.push({
    name: JVM,
    descriptionReference: "https://www.java.com/",
    descriptionLabel: "JVM",
    description: "виртуальная машина для выполнения Java байт-кода",
    icon: java,
    width: 50,
    height: 50
});

TECHNOLOGIES.push({
    name: KOTLIN,
    descriptionReference: "https://kotlinlang.org/",
    descriptionLabel: "Kotlin",
    description: "язык программирования (используется для backend разработки)",
    icon: kotlin,
    width: 50,
    height: 50
});

TECHNOLOGIES.push({
    name: GROOVY,
    descriptionReference: "http://groovy-lang.org/",
    descriptionLabel: "Groovy",
    description: "язык программирования (используется для backend разработки и написания тестов)",
    icon: groovy,
    width: 50,
    height: 50
});

TECHNOLOGIES.push({
    name: JS,
    descriptionReference: "https://developer.mozilla.org/ru/docs/Web/JavaScript",
    descriptionLabel: "JavaScript",
    description: "язык программирования для Web разработки",
    icon: js,
    width: 50,
    height: 50
});

TECHNOLOGIES.push({
    name: TS,
    descriptionReference: "https://www.typescriptlang.org/",
    descriptionLabel: "TypeScript",
    description: "язык программирования для Web разработки",
    icon: typescript,
    width: 50,
    height: 50
});

TECHNOLOGIES.push({
    name: ENVOY,
    descriptionReference: "https://www.envoyproxy.io/",
    descriptionLabel: "Envoy",
    description: "сетевой балансировщик и прокси",
    icon: envoy,
    width: 50,
    height: 50
});

TECHNOLOGIES.push({
    name: NGINX,
    descriptionReference: "https://nginx.org",
    descriptionLabel: "Nginx",
    description: "Web сервер",
    icon: nginx,
    width: 50,
    height: 50
});

TECHNOLOGIES.push({
    name: CADDY,
    descriptionReference: "https://caddyserver.com/",
    descriptionLabel: "Caddy",
    description: "Web сервер",
    icon: caddy,
    width: 50,
    height: 50
});

export const technologiesOf = (names: string[]) => names.flatMap(name => TECHNOLOGIES.filter(technology => technology.name == name))

export const technologyOf = (name: string) => TECHNOLOGIES.find(technology => technology.name == name)!


export const ARTIFACT_FORMATS = [DOCKER]
export const OPEN_SHIFT_ARTIFACT_FORMATS = [DOCKER];
export const DEFAULT_ARTIFACT_ARCHIVE_FORMAT = DOCKER;

export const artifactFormatsOf = (resourceId: ResourceIdentifier) => {
    switch (resourceId.type) {
        case ARTIFACTS_RESOURCE:
            return ARTIFACT_FORMATS
        case OPEN_SHIFT_RESOURCE:
            return OPEN_SHIFT_ARTIFACT_FORMATS
    }
    return [];
}

export const DOCKER_CONTAINERS = [NGINX, JVM]
export const DEFAULT_DOCKER_CONTAINER = NGINX;
