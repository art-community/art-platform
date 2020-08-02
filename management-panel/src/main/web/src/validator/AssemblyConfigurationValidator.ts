import {isEmptyArray, isNotEmptyArray} from "../framework/extensions/extensions";
import {DOCKER, GRADLE} from "../constants/TechnologyConstants";
import {AssemblyConfiguration} from "../model/AssemblyTypes";
import {DOCKER_IMAGE_REGEX} from "../constants/Regexps";

export const validateAssemblyConfiguration = (configuration: AssemblyConfiguration) => {
    if (!configuration.defaultResourceId || !configuration.technology || isEmptyArray(configuration.artifactConfigurations)) {
        return false
    }

    switch (configuration.technology) {
        case GRADLE:
            if (!configuration.gradleConfiguration) {
                return false
            }
            const properties = configuration.gradleConfiguration.properties;
            if (properties && isNotEmptyArray(properties)) {
                if (properties.some(property => !Boolean(property.name)) || properties.hasDuplicates(property => property.name)) {
                    return false;
                }
            }
            break;
        default:
            return false;
    }

    for (const artifactConfiguration of configuration.artifactConfigurations!) {
        let {archives, artifact, gradleConfiguration, name} = artifactConfiguration;
        if (!name || !artifact || isEmptyArray(archives)) {
            return false;
        }
        for (const archive of archives) {
            if (!archive.archiveTechnology) {
                return false;
            }
            switch (archive.archiveTechnology) {
                case DOCKER:
                    const docker = archive.dockerConfiguration;
                    if (!docker || !docker.image || !docker.image.match(DOCKER_IMAGE_REGEX) || !docker.containerTechnology) {
                        return false;
                    }
                    if (docker.sourcePaths && docker.sourcePaths.some(path => !path)) {
                        return false;
                    }
                    break;
                default:
                    return false;
            }
        }
        switch (configuration.technology) {
            case GRADLE:
                if (!gradleConfiguration || !gradleConfiguration.arguments) {
                    return false;
                }
                break;
            default:
                return false;
        }
    }

    return true
};
