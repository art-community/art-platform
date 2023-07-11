import {CONFIG_FILE_NAME_REGEX, ENTITY_NAME_REGEX, URL_REGEX} from "../constants/Regexps";
import {isNotEmptyArray} from "../framework/extensions/extensions";
import {ModuleConfigurationDraft, ModuleInformation} from "../model/ModuleTypes";
import {createModuleName} from "../service/ModuleService";
import {MAX_MODULE_NAME_SIZE} from "../constants/ModuleConstants";
import {CONFIGURATION_FILE_FORMATS} from "../constants/ConfigurationConstants";

export const validateModuleConfiguration = (configuration: ModuleConfigurationDraft, projectId: number, modules: ModuleInformation[]) => {
    const name = createModuleName(configuration);
    let {
        manualConfigurations,
        preparedConfigurations,
        hasStringConfigurations,
        hasParameters,
        hasPorts,
        hasUrl,
        parameters,
        ports,
        url
    } = configuration;

    const nameValid = Boolean(
        name
        && name.match(ENTITY_NAME_REGEX)
        && name.length < MAX_MODULE_NAME_SIZE
        && !modules.some(module => module.name == name && module.projectId == projectId)
    );

    const parametersValid = Boolean(
        !hasParameters || parameters
    )

    const portsValid = Boolean(
        !hasPorts || (isNotEmptyArray(ports) && (ports!.every(port => Boolean(port)) && !ports!.hasDuplicates()))
    );

    const urlValid = Boolean(
        !hasUrl || (url?.port && url.url?.match(URL_REGEX))
    );

    const regExp = CONFIG_FILE_NAME_REGEX(CONFIGURATION_FILE_FORMATS);
    const configurationFilesValid = Boolean(!hasStringConfigurations ||
        (
            isNotEmptyArray(manualConfigurations) && (manualConfigurations!.every(file => Boolean(file.name)
                && file.name.match(regExp) && file.content)
                && !manualConfigurations!.map(file => file.name).hasDuplicates())
        )
        || isNotEmptyArray(preparedConfigurations)
    );

    return Boolean(nameValid && parametersValid && portsValid && urlValid && configurationFilesValid)
};
