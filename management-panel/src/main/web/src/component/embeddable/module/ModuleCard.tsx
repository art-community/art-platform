import * as React from "react";
import {DispatchWithoutAction} from "react";
import StopOutlined from "@material-ui/icons/StopOutlined";
import SystemUpdateAltOutlined from "@material-ui/icons/SystemUpdateAltOutlined";
import LaunchOutlined from "@material-ui/icons/LaunchOutlined";
import SettingsOutlined from "@material-ui/icons/SettingsOutlined";
import DeleteOutlined from "@material-ui/icons/DeleteOutlined";
import SettingsBackupRestoreOutlined from "@material-ui/icons/SettingsBackupRestoreOutlined";
import LibraryAddOutlined from "@material-ui/icons/LibraryAddOutlined";
import AddToQueueOutlined from "@material-ui/icons/AddToQueueOutlined";
import DeleteSweepOutlined from "@material-ui/icons/DeleteSweepOutlined";
import {Theme, useTheme} from "@material-ui/core";
import {green, grey, lightBlue, orange, purple, red} from "@material-ui/core/colors";
import moment from "moment";
import {
    MODULE_INSTALLATION_STARTED_STATE,
    MODULE_INSTALLING_STATE,
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
} from "../../../constants/States";
import {calculateModuleStateChipStyles, calculateModuleStateColor, translateModuleStateToHuman} from "../../../constants/ModuleStateConstants";
import {asynchronous, isNotEmptyArray} from "../../../framework/extensions/extensions";
import {observe} from "../../../framework/pattern/Observable";
import {Configurable} from '../../../framework/pattern/Configurable';
import {Project} from "../../../model/ProjectTypes";
import {ResourcesStore} from "../../../loader/ResourcesLoader";
import {Module, ModuleInformation} from "../../../model/ModuleTypes";
import {Widget} from "../../../framework/widgets/Widget";
import {useModuleApi} from "../../../api/ModuleApi";
import {CardAvatarProperties} from '../../../framework/dsl/managed/card/CardAvatar';
import {CardMenuButton, CardMenuProperties} from "../../../framework/dsl/managed/card/CardMenu";
import {proxy} from "../../../framework/widgets/Proxy";
import {platform} from "../../entry/EntryPoint";
import {CodeEditorTheme, doNothing, SPACE} from "../../../framework/constants/Constants";
import {resourceIcon} from "../icon/ResourceIcon";
import {CardAttributeProperties} from "../../../framework/dsl/managed/card/CardAttribute";
import {versionIcon} from "../icon/VersionIcons";
import {assembledArtifactIcon} from "../icon/AssembledArtifactIcons";
import {DATE_TIME_FORMAT} from '../../../constants/DateTimeConstants';
import {chip, clickableChip, labelChip} from "../../../framework/dsl/simple/SimpleChip";
import {warning} from "../../../framework/dsl/managed/ManagedDialog";
import {card, ManagedCard} from "../../../framework/dsl/managed/card/ManagedCard";
import {onModuleUpdated} from "../../../streams/ModuleStream";
import {empty} from "../../../framework/dsl/simple/SimpleEmptyComponent";
import {useFileApi} from "../../../api/FileApi";
import {codeTooltip, deferredCodeTooltip} from "../../../framework/dsl/simple/SimpleCodeViewer";
// @ts-ignore
import downloadFile from "react-file-download"
import {encoder} from "../../../constants/EncodingService";
import {PlatformTheme} from "../../../constants/PlatformTheme";
import {grid, horizontalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {portMappingIcon, portMappingIcons} from "../icon/PortIcons";
import {optional} from "../../../framework/pattern/Optional";
import {moduleUpdatingDialog} from "./ModuleUpdatingDialog";
import {moduleCloningDialog} from "./ModuleCloningDialog";
import {projectIcon} from "../icon/ProjectIcon";
import {ApplicationsStore} from "../../../loader/ApplicationsLoader";
import {applicationIcon} from "../icon/ApplicationIcon";
import {usePreparedConfigurationApi} from "../../../api/PreparedConfigurationApi";
import {PreparedConfigurationIdentifier} from "../../../model/PreparedConfigurationTypes";

const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        installingChip: {
            backgroundColor: purple['800'],
            color: theme.palette.common.white
        },
        changingChip: {
            backgroundColor: orange['700'],
            color: theme.palette.common.white
        },
        stoppedChip: {
            backgroundColor: lightBlue["700"],
            color: theme.palette.common.white
        },
        notInstalledChip: {
            backgroundColor: grey["700"],
            color: theme.palette.common.white
        },
        invalidChip: {
            backgroundColor: red["700"],
            color: theme.palette.common.white
        },
        runningChip: {
            backgroundColor: green["700"],
            color: theme.palette.common.white
        }
    }))
};

type Properties = {
    project: Project
    module: ModuleInformation
    resources: ResourcesStore
    applications: ApplicationsStore
    modules: ModuleInformation[]
    preparedConfigurations: PreparedConfigurationIdentifier[]
    expanded?: boolean
}

class Configuration extends Configurable<Properties> {
    module = this.property<Module>();
}

export class ModuleCard extends Widget<ModuleCard, Properties, Configuration> {
    #card?: ManagedCard;

    #module = () => this.configuration.module.value || this.properties.module;

    #theme = this.hookValue(useTheme);

    #style = this.hookValue(useStyle);

    #moduleApi = this.hookValue(useModuleApi);

    #fileApi = this.hookValue(useFileApi);

    #preparedConfigurationApi = this.hookValue(usePreparedConfigurationApi);

    #stateLabelStyle = () => {
        const state = this.#module().state;
        return calculateModuleStateChipStyles(state);
    };

    #stateLabel = () => {
        const state = this.#module().state;
        return translateModuleStateToHuman(state);
    };

    #stateBackground = (theme: Theme) => {
        const state = this.#module().state;
        return calculateModuleStateColor(state, theme);
    };

    #loadModule = (onComplete: DispatchWithoutAction = doNothing) => {
        if (this.configuration.module.value) {
            return
        }
        asynchronous(() => this.#moduleApi().getModule(this.properties.module.id, module => {
            this.configuration.module.set(module)
            onComplete();
        }));
    }

    #startUpdating = () => {
        if (!this.configuration.module.value) {
            this.#loadModule(this.#updatingDialog.spawn);
            return;
        }
        this.#updatingDialog.spawn();
    }

    #startCloning = () => {
        if (!this.configuration.module.value) {
            this.#loadModule(this.#cloningDialog.spawn);
            return;
        }
        this.#cloningDialog.spawn();
    }

    #avatar = (theme: Theme): CardAvatarProperties => {
        switch (this.#module().state) {
            case MODULE_INSTALLING_STATE:
            case MODULE_INSTALLATION_STARTED_STATE:
                return {
                    progress: {
                        color: this.#stateBackground(theme)
                    }
                };
            case MODULE_UPDATE_STARTED_STATE:
            case MODULE_STOP_STARTED_STATE:
            case MODULE_RESTART_STARTED_STATE:
            case MODULE_UNINSTALL_STARTED_STATE:
            case MODULE_UPDATING_STATE:
            case MODULE_STOPPING_STATE:
            case MODULE_RESTARTING_STATE:
            case MODULE_UNINSTALLING_STATE:
                return {
                    progress: {
                        color: this.#stateBackground(theme)
                    }
                };
        }
        return {
            letter: {
                firstLetter: this.#module().name[0],
                background: this.#stateBackground(theme)
            }
        };
    };

    #menu = (): CardMenuProperties => {
        const buttons: CardMenuButton[] =
            [
                {
                    tooltip: "Клонировать",
                    icon: proxy(<LibraryAddOutlined color={"primary"}/>),
                    onClick: () => this.#startCloning()
                }
            ];
        switch (this.#module().state) {
            case MODULE_INSTALLATION_STARTED_STATE:
            case MODULE_UPDATE_STARTED_STATE:
            case MODULE_STOP_STARTED_STATE:
            case MODULE_RESTART_STARTED_STATE:
            case MODULE_UNINSTALL_STARTED_STATE:
            case MODULE_UPDATING_STATE:
            case MODULE_STOPPING_STATE:
            case MODULE_RESTARTING_STATE:
            case MODULE_UNINSTALLING_STATE:
            case MODULE_INSTALLING_STATE:
                return {
                    actions: {
                        buttons: buttons
                        .with({
                            tooltip: "Удалить",
                            icon: proxy(<DeleteOutlined color={"primary"}/>),
                            onClick: () => this.#deletingDialog.open()
                        })
                    }
                };
            case MODULE_RUN_STATE:
                return {
                    actions: {
                        buttons: buttons
                        .with({
                            tooltip: "Обновить",
                            icon: proxy(<SystemUpdateAltOutlined color={"primary"}/>),
                            onClick: () => this.#moduleApi().refreshModuleArtifact(this.#module().id)
                        })
                        .with({
                            tooltip: "Изменить",
                            icon: proxy(<SettingsOutlined color={"primary"}/>),
                            onClick: () => this.#startUpdating()
                        })
                        .with({
                            tooltip: "Перезапустить",
                            icon: proxy(<SettingsBackupRestoreOutlined color={"primary"}/>),
                            onClick: () => this.#moduleApi().restartModule(this.#module().id)
                        })
                        .with({
                            tooltip: "Остановить",
                            icon: proxy(<StopOutlined color={"primary"}/>),
                            onClick: () => this.#moduleApi().stopModule(this.#module().id)
                        })
                        .with({
                            tooltip: "Удалить с ресурса",
                            icon: proxy(<DeleteSweepOutlined color={"primary"}/>),
                            onClick: () => this.#moduleApi().deleteModuleFromResource(this.#module().id)
                        })
                        .with({
                            tooltip: "Удалить",
                            icon: proxy(<DeleteOutlined color={"primary"}/>),
                            onClick: () => this.#deletingDialog.open()
                        })
                    }
                }
            case MODULE_STOPPED_STATE:
                return {
                    actions: {
                        buttons: buttons
                        .with({
                            tooltip: "Запустить",
                            icon: proxy(<LaunchOutlined color={"primary"}/>),
                            onClick: () => this.#moduleApi().restartModule(this.#module().id)
                        })
                        .with({
                            tooltip: "Изменить",
                            icon: proxy(<SettingsOutlined color={"primary"}/>),
                            onClick: () => this.#startUpdating()
                        })
                        .with({
                            tooltip: "Обновить и запустить",
                            icon: proxy(<SystemUpdateAltOutlined color={"primary"}/>),
                            onClick: () => this.#moduleApi().refreshModuleArtifact(this.#module().id)
                        })
                        .with({
                            tooltip: "Удалить с ресурса",
                            icon: proxy(<DeleteSweepOutlined color={"primary"}/>),
                            onClick: () => this.#moduleApi().deleteModuleFromResource(this.#module().id)
                        })
                        .with({
                            tooltip: "Удалить",
                            icon: proxy(<DeleteOutlined color={"primary"}/>),
                            onClick: () => this.#deletingDialog.open()
                        })
                    }
                }
            case MODULE_NOT_INSTALLED_STATE:
                buttons
                .with({
                    tooltip: "Изменить",
                    icon: proxy(<SettingsOutlined color={"primary"}/>),
                    onClick: () => this.#startUpdating()
                })
                .with({
                    tooltip: "Установить повторно",
                    icon: proxy(<AddToQueueOutlined color={"primary"}/>),
                    onClick: () => this.#moduleApi().reinstallModule(this.#module().id)
                })
                .with({
                    tooltip: "Удалить",
                    icon: proxy(<DeleteOutlined color={"primary"}/>),
                    onClick: () => this.#deletingDialog.open()
                })
                return {actions: {buttons}}
        }
        return {
            actions: {
                buttons: buttons
                .with({
                    tooltip: "Изменить",
                    icon: proxy(<SettingsOutlined color={"primary"}/>),
                    onClick: () => this.#startUpdating()
                })
                .with({
                    tooltip: "Установить повторно",
                    icon: proxy(<AddToQueueOutlined color={"primary"}/>),
                    onClick: () => this.#moduleApi().reinstallModule(this.#module().id)
                })
                .with({
                    tooltip: "Удалить с ресурса",
                    icon: proxy(<DeleteSweepOutlined color={"primary"}/>),
                    onClick: () => this.#moduleApi().deleteModuleFromResource(this.#module().id)
                })
                .with({
                    tooltip: "Удалить",
                    icon: proxy(<DeleteOutlined color={"primary"}/>),
                    onClick: () => this.#deletingDialog.open()
                })
            }
        };
    };

    #attributes = () => {
        const {
            artifact,
            count,
            internalIp,
            externalId,
            portMappings,
            url,
            parameters,
            resourceId,
            state,
            manualConfigurations,
            preparedConfigurations,
            additionalFiles,
            applications,
            updateTimeStamp
        } = this.#module();

        const attributes: CardAttributeProperties[] = [{name: "Проект", icon: projectIcon(this.properties.project.name)}];

        const urlPort = portMappings?.find(mapping => mapping.internalPort == url?.port)
        if (state != MODULE_INSTALLING_STATE && url?.url && urlPort) {
            attributes
            .with({name: "URL", value: url.url, link: true})
            .with({name: "URL активирован по порту", icon: portMappingIcon(urlPort)})
        }

        const portsWithoutUrl = portMappings!.filter(mapping => mapping.internalPort != url?.port);
        if (isNotEmptyArray(portsWithoutUrl)) {
            attributes.with({name: "Порты", icon: portMappingIcons(portsWithoutUrl)})
        }

        if (module) {
            attributes
            .with({name: "Количество", icon: chip({label: count.toString(), color: "primary"})})
            .with({name: "Версия", icon: versionIcon(artifact.version.toString())})
            .with({name: "Артефакт", icon: assembledArtifactIcon({artifact})})
        }

        if (externalId && resourceId) {
            attributes
            .with({name: "Ресурс", icon: resourceIcon({name: resourceId.name, type: resourceId.type})})
            .with({name: "Идентификатор на ресурсе", value: externalId.id})
        }

        if (internalIp) {
            attributes
            .with({name: "IP на ресурсе", value: internalIp})
        }

        if (parameters) {
            attributes.with({
                name: "Параметры запуска",
                icon: grid({spacing: 1}).pushWidgets(parameters.trim()
                    .split(SPACE)
                    .filter(parameter => parameter.trim())
                    .map(parameter => labelChip(parameter, {color: "primary"}))
                )
            })
        }

        if (updateTimeStamp) {
            attributes
            .with({name: "Метка обновления", value: moment.unix(updateTimeStamp).format(DATE_TIME_FORMAT)})
        }

        if (isNotEmptyArray(preparedConfigurations) || isNotEmptyArray(manualConfigurations)) {
            const prepared = preparedConfigurations!.map(id => {
                    const chip = clickableChip(
                        id.name,
                        () => this.#preparedConfigurationApi()
                        .getPreparedConfiguration(id.id, configuration => downloadFile(encoder().encode(configuration.configuration), id.name)),
                        {color: "primary"}
                    );
                    return deferredCodeTooltip(
                        chip,
                        setter => this.#preparedConfigurationApi()
                        .getPreparedConfiguration(id.id, configuration => setter(configuration.configuration)), {
                            fileName: id.name,
                            themeName: platform.themeName() == PlatformTheme.DARK
                                ? CodeEditorTheme.DARK
                                : CodeEditorTheme.LIGHT
                        }
                    );
                }
            );
            const manual = manualConfigurations!.map(configuration => {
                    const chip = clickableChip(configuration.name, () => downloadFile(encoder().encode(configuration.content), configuration.name),
                        {color: "primary"}
                    );
                    return codeTooltip(chip, {
                            value: configuration.content,
                            fileName: configuration.name,
                            themeName: platform.themeName() == PlatformTheme.DARK ? CodeEditorTheme.DARK : CodeEditorTheme.LIGHT
                        }
                    );
                }
            );
            attributes.with({
                name: "Конфигурации",
                icon: horizontalGrid({spacing: 1, wrap: "nowrap"}).pushWidgets([...prepared, ...manual])
            });
        }

        if (isNotEmptyArray(additionalFiles)) {
            const chips = additionalFiles!.map(id => clickableChip(
                id.name,
                () => this.#fileApi().getFile(id.id, file => downloadFile(file.bytes, id.name)),
                {color: "secondary"})
            );
            attributes.with({
                name: "Файлы",
                icon: horizontalGrid({spacing: 1, wrap: "nowrap"}).pushWidgets(chips)
            })
        }

        if (isNotEmptyArray(applications)) {
            const chips = applications!.map(application => applicationIcon({name: application.applicationId.name, type: application.applicationId.type}))
            attributes.with({
                name: "Приложения",
                icon: horizontalGrid({spacing: 1, wrap: "nowrap"}).pushWidgets(chips)
            })
        }

        return attributes
        .with({
            custom: chip({
                label: this.#stateLabel(),
                style: this.#style()[this.#stateLabelStyle()]
            })
        });
    };

    #deletingDialog = this.add(warning({
        label: "Вы уверены, что хотите удалить модуль?",
        warning: "Удаленными модулями нельзя воспользоваться",
        cancelLabel: "Нет, оставить",
        approveLabel: "Да, удалить",
        onCancel: () => this.#deletingDialog.close(),
        onApprove: () => this.#moduleApi().deleteModule(this.#module().id)
    }));

    #updatingDialog = this.add(optional(() => moduleUpdatingDialog({
        resources: this.properties.resources,
        baseModule: this.configuration.module.value!,
        modules: this.properties.modules,
        applications: this.properties.applications,
        preparedConfigurations: this.properties.preparedConfigurations
    })));

    #cloningDialog = this.add(optional(() => moduleCloningDialog({
        resources: this.properties.resources,
        baseModule: this.configuration.module.value!,
        modules: this.properties.modules,
        applications: this.properties.applications,
        preparedConfigurations: this.properties.preparedConfigurations
    })));

    constructor(properties?: Properties) {
        super(properties, Configuration);
        const id = this.#module().id;
        this.widgetName = `(${this.constructor.name}): ${id}`
        this.configuration.module.consume(() => {
            this.#card
            ?.configureAvatar(avatar => avatar.setAvatar(this.#avatar(this.#theme())))
            ?.configureMenu(menu => menu.setMenu(this.#menu()))
            ?.setAttributes(this.#attributes())
        })
        this.onLoad(() => {
            this.#card = card({
                label: `Модуль ${this.#module().name}`,
                expanded: this.properties.expanded
            })
            .configureAvatar(avatar => avatar.setAvatar(this.#avatar(this.#theme())))
            .configureMenu(menu => menu.setMenu(this.#menu()))
            .setAttributes(this.#attributes())
            .onExpansionChanged(expanded => expanded && this.#loadModule())
        })
        this.subscribe(() => onModuleUpdated(id, this.configuration.module.set));

        platform.onThemeChanged(theme => this.#card
        ?.configureAvatar(avatar => avatar.setAvatar(this.#avatar(theme)))
        ?.setAttributes(this.#attributes()))
    }

    key = () => this.properties.module.id;

    expanded = () => this.#card?.expanded();

    draw = () => this.#card?.render() || empty().render();
}

export const moduleCard = (properties: Properties) => new ModuleCard(properties);
