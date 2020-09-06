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
} from "./States";
import {Theme} from "@material-ui/core";
import {green, grey, lightBlue, orange, purple, red} from "@material-ui/core/colors";
import {PlatformTheme} from "./PlatformTheme";

export const MODULE_INSTALLING_BACKGROUND_LIGHT = purple['800'];
export const MODULE_CHANGING_BACKGROUND_LIGHT = orange['700'];
export const MODULE_STOPPED_BACKGROUND_LIGHT = lightBlue["700"];
export const MODULE_NOT_INSTALLED_BACKGROUND_LIGHT = grey["700"];
export const MODULE_INVALID_BACKGROUND_LIGHT = red["700"];
export const MODULE_RUNNING_BACKGROUND_LIGHT = green["700"];

export const MODULE_INSTALLING_BACKGROUND_DARK = purple['800'];
export const MODULE_CHANGING_BACKGROUND_DARK = orange['700'];
export const MODULE_STOPPED_BACKGROUND_DARK = lightBlue["700"];
export const MODULE_NOT_INSTALLED_BACKGROUND_DARK = grey["700"];
export const MODULE_INVALID_BACKGROUND_DARK = red["700"];
export const MODULE_RUNNING_BACKGROUND_DARK = green["700"]

export const MODULE_FILTERABLE_STATES = [
    [
        {state: MODULE_INSTALLING_STATE, label: "Устанавливается", color: purple['800']},
        {state: MODULE_RUN_STATE, label: "Запущен", color: green["700"]},
        {state: MODULE_INVALID_STATE, label: "Поврежден", color: red["700"]},
        {state: MODULE_STOPPED_STATE, label: "Остановлен", color: lightBlue["700"]},
        {state: MODULE_NOT_INSTALLED_STATE, label: "Не установлен", color: grey["700"]},
    ],
    [
        {state: MODULE_RESTARTING_STATE, label: "Запускается", color: orange['700']},
        {state: MODULE_UPDATING_STATE, label: "Обновляется", color: orange['700']},
        {state: MODULE_STOPPING_STATE, label: "Останавливается", color: orange['700']},
        {state: MODULE_UNINSTALLING_STATE, label: "Удаляется", color: orange['700']}
    ]
];

export const translateModuleStateToHuman = (state: string) => {
    switch (state) {
        case MODULE_INSTALLATION_STARTED_STATE:
        case MODULE_INSTALLING_STATE:
            return "Устанавливается";
        case MODULE_UPDATE_STARTED_STATE:
        case MODULE_UPDATING_STATE:
            return "Обновляется";
        case MODULE_STOP_STARTED_STATE:
        case MODULE_STOPPING_STATE:
            return "Останавливается";
        case MODULE_RESTART_STARTED_STATE:
        case MODULE_RESTARTING_STATE:
            return "Запускается";
        case MODULE_UNINSTALL_STARTED_STATE:
        case MODULE_UNINSTALLING_STATE:
            return "Удаляется";
        case MODULE_STOPPED_STATE:
            return "Остановлен";
        case MODULE_NOT_INSTALLED_STATE:
            return "Не установлен";
        case MODULE_INVALID_STATE:
            return "Поврежден";
        case MODULE_RUN_STATE:
            return "Запущен";
        default:
            return "Поврежден";
    }
};

export const calculateModuleStateColor = (state: string, theme: Theme) => {
    switch (state) {
        case MODULE_INSTALLATION_STARTED_STATE:
        case MODULE_INSTALLING_STATE:
            return theme.palette.type == PlatformTheme.DARK ? MODULE_INSTALLING_BACKGROUND_DARK : MODULE_INSTALLING_BACKGROUND_LIGHT;
        case MODULE_UPDATE_STARTED_STATE:
        case MODULE_UPDATING_STATE:
        case MODULE_RESTART_STARTED_STATE:
        case MODULE_RESTARTING_STATE:
        case MODULE_UNINSTALL_STARTED_STATE:
        case MODULE_UNINSTALLING_STATE:
        case MODULE_STOP_STARTED_STATE:
        case MODULE_STOPPING_STATE:
            return theme.palette.type == PlatformTheme.DARK ? MODULE_CHANGING_BACKGROUND_DARK : MODULE_CHANGING_BACKGROUND_LIGHT;
        case MODULE_STOPPED_STATE:
            return theme.palette.type == PlatformTheme.DARK ? MODULE_STOPPED_BACKGROUND_DARK : MODULE_STOPPED_BACKGROUND_LIGHT;
        case MODULE_NOT_INSTALLED_STATE:
            return theme.palette.type == PlatformTheme.DARK ? MODULE_NOT_INSTALLED_BACKGROUND_DARK : MODULE_NOT_INSTALLED_BACKGROUND_LIGHT;
        case MODULE_INVALID_STATE:
            return theme.palette.type == PlatformTheme.DARK ? MODULE_INVALID_BACKGROUND_DARK : MODULE_INVALID_BACKGROUND_LIGHT;
        case MODULE_RUN_STATE:
            return theme.palette.type == PlatformTheme.DARK ? MODULE_RUNNING_BACKGROUND_DARK : MODULE_RUNNING_BACKGROUND_LIGHT;
    }
    return theme.palette.type == PlatformTheme.DARK ? MODULE_INVALID_BACKGROUND_DARK : MODULE_INVALID_BACKGROUND_LIGHT;
};

export const calculateModuleStateChipStyles = (state: string) => {
    switch (state) {
        case MODULE_INSTALLATION_STARTED_STATE:
        case MODULE_INSTALLING_STATE:
            return "installingChip";
        case MODULE_UPDATE_STARTED_STATE:
        case MODULE_UPDATING_STATE:
        case MODULE_RESTART_STARTED_STATE:
        case MODULE_RESTARTING_STATE:
        case MODULE_UNINSTALL_STARTED_STATE:
        case MODULE_UNINSTALLING_STATE:
        case MODULE_STOP_STARTED_STATE:
        case MODULE_STOPPING_STATE:
            return "changingChip";
        case MODULE_STOPPED_STATE:
            return "stoppedChip";
        case MODULE_NOT_INSTALLED_STATE:
            return "notInstalledChip";
        case MODULE_INVALID_STATE:
            return "invalidChip";
        case MODULE_RUN_STATE:
            return "runningChip";
    }
    return "invalidChip";
};

export const calculateModuleStateChipColor = (state: string) => {
    switch (state) {
        case MODULE_INSTALLATION_STARTED_STATE:
        case MODULE_INSTALLING_STATE:
            return purple['800'];
        case MODULE_UPDATE_STARTED_STATE:
        case MODULE_UPDATING_STATE:
        case MODULE_RESTART_STARTED_STATE:
        case MODULE_RESTARTING_STATE:
        case MODULE_UNINSTALL_STARTED_STATE:
        case MODULE_UNINSTALLING_STATE:
        case MODULE_STOP_STARTED_STATE:
        case MODULE_STOPPING_STATE:
            return orange['700'];
        case MODULE_STOPPED_STATE:
            return lightBlue["700"];
        case MODULE_NOT_INSTALLED_STATE:
            return grey["700"];
        case MODULE_INVALID_STATE:
            return red["700"];
        case MODULE_RUN_STATE:
            return green["700"];
    }
    return green["700"];
};
