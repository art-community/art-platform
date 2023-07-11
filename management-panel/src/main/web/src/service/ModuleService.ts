import {Module, ModuleConfiguration, ModuleConfigurationDraft, ModuleInformation} from "../model/ModuleTypes";
import {
    MODULE_INSTALLATION_STARTED_STATE,
    MODULE_INSTALLING_STATE,
    MODULE_INVALID_STATE,
    MODULE_NOT_INSTALLED_STATE,
    MODULE_RESTART_STARTED_STATE,
    MODULE_RESTARTING_STATE,
    MODULE_RUN_STATE,
    MODULE_STOP_STARTED_STATE,
    MODULE_STOPPED_STATE,
    MODULE_STOPPING_STATE,
    MODULE_UNINSTALL_STARTED_STATE,
    MODULE_UNINSTALLING_STATE,
    MODULE_UPDATE_STARTED_STATE,
    MODULE_UPDATING_STATE
} from "../constants/States";

export const moduleIsChanging = (module: Module | ModuleInformation) => {
    switch (module.state) {
        case MODULE_NOT_INSTALLED_STATE:
        case MODULE_INVALID_STATE:
        case MODULE_STOPPED_STATE:
        case MODULE_RUN_STATE:
            return false;
        case MODULE_INSTALLATION_STARTED_STATE:
        case MODULE_UPDATE_STARTED_STATE:
        case MODULE_RESTARTING_STATE:
        case MODULE_UPDATING_STATE:
        case MODULE_INSTALLING_STATE:
        case MODULE_UNINSTALL_STARTED_STATE:
        case MODULE_RESTART_STARTED_STATE:
        case MODULE_STOP_STARTED_STATE:
        case MODULE_STOPPING_STATE:
        case MODULE_UNINSTALLING_STATE:
            return true;
    }
    return false;
};

export const createModuleName = (configuration: ModuleConfiguration | ModuleConfigurationDraft) => configuration.name
    ? configuration.name
    : `${configuration.artifact.name}-${configuration.artifact.version}`;
