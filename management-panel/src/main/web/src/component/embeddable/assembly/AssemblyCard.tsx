import * as React from "react";
import CancelOutlined from "@material-ui/icons/CancelOutlined";
import BlockOutlined from "@material-ui/icons/BlockOutlined";
import ViewModuleTwoTone from "@material-ui/icons/ViewModuleTwoTone";
import Done from "@material-ui/icons/Done";
import SettingsBackupRestoreOutlined from "@material-ui/icons/SettingsBackupRestoreOutlined";
import StorageOutlined from "@material-ui/icons/StorageOutlined";
import moment from "moment";
import DeleteOutlined from "@material-ui/icons/DeleteOutlined";
import {
    ASSEMBLY_BUILDING_STATE,
    ASSEMBLY_CANCELED_STATE,
    ASSEMBLY_DONE_STATE,
    ASSEMBLY_FAILED_STATE,
    ASSEMBLY_RESTARTED_STATE,
    ASSEMBLY_STARTED_ON_RESOURCE_STATE,
    ASSEMBLY_STARTED_STATE
} from "../../../constants/States";
import {calculateDuration} from "../../../calculator/DurationCalculator";
import {Theme, useTheme} from "@material-ui/core";
import {useAssemblyApi} from "../../../api/AssemblyApi";
import {asynchronous, isNotEmptyArray} from "../../../framework/extensions/extensions";
import {green} from "@material-ui/core/colors";
import {ResourcesStore} from "../../../loader/ResourcesLoader";
import {Configurable} from "../../../framework/pattern/Configurable";
import {Widget} from "../../../framework/widgets/Widget";
import {arrayCache} from "../../../framework/pattern/ArrayCache";
import {proxy} from "../../../framework/widgets/Proxy";
import {CardMenuButton, CardMenuProperties} from "../../../framework/dsl/managed/card/CardMenu";
import {platform} from "../../entry/EntryPoint";
import {CardAttributeProperties} from "../../../framework/dsl/managed/card/CardAttribute";
import {resourceIcon} from "../icon/ResourceIcon";
import {warning} from "../../../framework/dsl/managed/ManagedDialog";
import {Project} from "../../../model/ProjectTypes";
import {Assembly, AssemblyInformation} from "../../../model/AssemblyTypes";
import {assembledArtifactsIcons} from "../icon/AssembledArtifactIcons";
import {artifactConfigurationIcons} from "../icon/ArtifactConfigurationIcon";
import {CardAvatarProperties} from "../../../framework/dsl/managed/card/CardAvatar";
import {technologyIcon} from "../icon/TechnologyIcons";
import {card, ManagedCard} from "../../../framework/dsl/managed/card/ManagedCard";
import {onAssemblyUpdated} from "../../../streams/AssemblyStream";
import {PlatformLogDialog, platformLogDialog} from "./PlatformLogDialog";
import {DATE_TIME_FORMAT} from "../../../constants/DateTimeConstants";
import {empty} from "../../../framework/dsl/simple/SimpleEmptyComponent";
import {ModulesInstallationDialog, modulesInstallationDialog} from "../module/ModulesInstallationDialog";
import {MODULES_PATH} from "../../../constants/Routers";
import {projectIcon} from "../icon/ProjectIcon";
import {chip} from "../../../framework/dsl/simple/SimpleChip";
import {ApplicationsStore} from "../../../loader/ApplicationsLoader";
import {optional} from "../../../framework/pattern/Optional";
import {PreparedConfigurationIdentifier} from "../../../model/PreparedConfigurationTypes";
import {assemblyIsRunning} from "../../../service/AssemblyService";

interface Properties {
    project: Project
    projects: Project[]
    assembly: AssemblyInformation
    resources: ResourcesStore
    applications: ApplicationsStore
    preparedConfigurations: PreparedConfigurationIdentifier[]
}

class Configuration extends Configurable<Properties> {
    assembly = this.property<Assembly>();
}

export class AssemblyCard extends Widget<AssemblyCard, Properties, Configuration> {
    #card?: ManagedCard;

    #assembly = () => this.configuration.assembly.value || this.properties.assembly;

    #theme = this.hookValue(useTheme);

    #api = this.hookValue(useAssemblyApi);

    #assembledArtifactsCache = arrayCache(
        () => this.#assembly().artifacts,
        artifacts => assembledArtifactsIcons(artifacts, true)
    )

    #expectedArtifactsCache = arrayCache(
        () => this.configuration.assembly.value!.artifactConfigurations,
        artifacts => artifactConfigurationIcons(artifacts)
    )

    #loadAssembly = () => {
        if (this.configuration.assembly.value) {
            return
        }
        asynchronous(() => this.#api().getAssembly(this.properties.assembly.id, this.configuration.assembly.set));
    }

    #logsDialog = this.add(optional<PlatformLogDialog>(() => platformLogDialog({
        label: `Логи сборки ${this.#assembly().version.version}`,
        loading: assemblyIsRunning(this.#assembly()),
        logId: this.#assembly().logId
    })))

    #modulesDialog = this.add(optional<ModulesInstallationDialog>(() => modulesInstallationDialog({
            resources: this.properties.resources,
            projects: this.properties.projects,
            project: this.properties.project,
            artifacts: this.#assembly().artifacts,
            applications: this.properties.applications,
            preparedConfigurations: this.properties.preparedConfigurations
        })
        .onInstall(() => platform.redirect(MODULES_PATH)))
    )

    #avatar = (theme: Theme): CardAvatarProperties => {
        switch (this.#assembly().state) {
            case ASSEMBLY_STARTED_STATE:
            case ASSEMBLY_RESTARTED_STATE:
                return {
                    progress: {
                        color: theme.palette.primary.main
                    }
                };
            case ASSEMBLY_STARTED_ON_RESOURCE_STATE:
            case ASSEMBLY_BUILDING_STATE:
                return {
                    progress: {
                        color: green["800"]
                    }
                };
            case ASSEMBLY_FAILED_STATE:
                return {
                    icon: proxy(<CancelOutlined htmlColor={"red"}/>)
                };
            case ASSEMBLY_CANCELED_STATE:
                return {
                    icon: proxy(<BlockOutlined htmlColor={"gray"}/>)
                }
            case ASSEMBLY_DONE_STATE:
                return {
                    icon: proxy(<Done htmlColor={"green"}/>)
                }
        }
        return {
            progress: {
                color: theme.palette.primary.main
            }
        };
    };

    #menu = (): CardMenuProperties => {
        const buttons: CardMenuButton[] = [{
            tooltip: "Удалить",
            icon: proxy(<DeleteOutlined color={"primary"}/>),
            onClick: () => this.#deletingDialog.open()
        }];
        switch (this.#assembly().state) {
            case ASSEMBLY_RESTARTED_STATE:
            case ASSEMBLY_STARTED_STATE:
                return {
                    actions: {
                        buttons: buttons.with({
                            tooltip: "Отменить",
                            icon: proxy(<CancelOutlined color={"primary"}/>),
                            onClick: () => this.#cancellationDialog.open()
                        })
                    }
                };
            case ASSEMBLY_STARTED_ON_RESOURCE_STATE:
            case ASSEMBLY_BUILDING_STATE:
                return {
                    actions: {
                        buttons: buttons
                        .with({
                            tooltip: "Показать логи",
                            icon: proxy(<StorageOutlined color={"primary"}/>),
                            onClick: () => this.#logsDialog.spawn()
                        })
                        .with({
                            tooltip: "Отменить",
                            icon: proxy(<CancelOutlined color={"primary"}/>),
                            onClick: () => this.#cancellationDialog.open()
                        })
                    }
                }
            case ASSEMBLY_DONE_STATE:
                return {
                    actions: {
                        buttons: buttons
                        .with({
                            tooltip: "Установить модули",
                            icon: proxy(<ViewModuleTwoTone color={"primary"}/>),
                            onClick: () => this.#modulesDialog.spawn()
                        })
                        .with({
                            tooltip: "Показать логи",
                            icon: proxy(<StorageOutlined color={"primary"}/>),
                            onClick: () => this.#logsDialog.spawn()
                        })
                        .with({
                            tooltip: "Перезапустить",
                            icon: proxy(<SettingsBackupRestoreOutlined color={"primary"}/>),
                            onClick: () => this.#api().rebuildProject(this.properties.assembly.id)
                        })
                    }
                }
            case ASSEMBLY_FAILED_STATE:
            case ASSEMBLY_CANCELED_STATE:
                return {
                    actions: {
                        buttons: buttons
                        .with({
                            tooltip: "Показать логи",
                            icon: proxy(<StorageOutlined color={"primary"}/>),
                            onClick: () => this.#logsDialog.spawn()
                        })
                        .with({
                            tooltip: "Перезапустить",
                            icon: proxy(<SettingsBackupRestoreOutlined color={"primary"}/>),
                            onClick: () => this.#api().rebuildProject(this.properties.assembly.id)
                        })
                    }
                }
        }
        return {actions: {buttons}}
    };

    #attributes = () => {
        const {
            endTimeStamp,
            resourceId,
            startTimeStamp,
            state,
            technology,
            version
        } = this.#assembly();
        const attributes: CardAttributeProperties[] = [];

        attributes.with({name: "Проект", icon: projectIcon(this.properties.project.name)});
        if (resourceId) {
            attributes.with({name: "Ресурс", icon: resourceIcon({name: resourceId.name, type: resourceId.type})})
        }
        if (technology) {
            attributes.with({name: "Технология", icon: technologyIcon(technology, true)});
        }
        if (isNotEmptyArray(this.configuration.assembly.value?.artifacts)) {
            attributes.with({name: "Собранные артефакты", icon: this.#assembledArtifactsCache()})
        }
        if (isNotEmptyArray(this.configuration.assembly.value?.artifactConfigurations) && version) {
            attributes.with({name: "Ожидаемые артефакты", icon: this.#expectedArtifactsCache()})
        }
        if (!startTimeStamp || !endTimeStamp) {
            return attributes;
        }
        if (startTimeStamp) {
            attributes.with({name: "Метка запуска", value: moment.unix(startTimeStamp).format(DATE_TIME_FORMAT)});
        }
        switch (state) {
            case ASSEMBLY_FAILED_STATE:
                attributes
                .with({name: "Метка падения", value: moment.unix(endTimeStamp).format(DATE_TIME_FORMAT)})
                .with({name: "Длительность", icon: chip({label: calculateDuration(endTimeStamp, startTimeStamp), color: "primary"})})
                break;
            case ASSEMBLY_DONE_STATE:
                attributes
                .with({name: "Метка завершения", value: moment.unix(endTimeStamp).format(DATE_TIME_FORMAT)})
                .with({name: "Длительность", icon: chip({label: calculateDuration(endTimeStamp, startTimeStamp), color: "primary"})})
                break;
            case ASSEMBLY_CANCELED_STATE:
                attributes
                .with({name: "Метка отмены", value: moment.unix(endTimeStamp).format(DATE_TIME_FORMAT)})
                .with({name: "Длительность", icon: chip({label: calculateDuration(endTimeStamp, startTimeStamp), color: "primary"})})
        }
        return attributes;
    };

    #deletingDialog = this.add(warning({
        label: "Вы уверены, что хотите удалить сборку?",
        warning: "Удаление сборки не позволит смотреть ее логи",
        cancelLabel: "Нет, оставить",
        approveLabel: "Да, удалить",
        onCancel: () => this.#deletingDialog.close(),
        onApprove: () => this.#api().deleteAssembly(this.properties.assembly.id)
    }));

    #cancellationDialog = this.add(warning({
        label: "Вы уверены, что хотите отменить сборку?",
        cancelLabel: "Нет, пусть собирается",
        approveLabel: "Да, отменить",
        onCancel: () => this.#cancellationDialog.close(),
        onApprove: () => this.#api().cancelAssembly(this.properties.assembly.id)
    }));

    constructor(properties: Properties) {
        super(properties, Configuration);
        const id = this.properties.assembly.id;
        this.widgetName = `[${this.constructor.name}]: ${id}`
        this.configuration.assembly.consume(assembly => {
            this.#card
            ?.configureAvatar(avatar => avatar.setAvatar(this.#avatar(this.#theme())))
            ?.configureMenu(menu => menu.setMenu(this.#menu()))
            ?.setAttributes(this.#attributes())

            this.#logsDialog.get()?.setLoading(assemblyIsRunning(assembly!))
        })
        this.onLoad(() => {
            const expanded = assemblyIsRunning(this.properties.assembly);
            this.#card = card({label: `Сборка версии ${this.properties.assembly.version.version}`, expanded})
            .configureAvatar(avatar => avatar.setAvatar(this.#avatar(this.#theme())))
            .configureMenu(menu => menu.setMenu(this.#menu()))
            .setAttributes(this.#attributes())
            .onExpansionChanged(this.#loadAssembly)
            expanded && this.#loadAssembly();
        })
        platform.onThemeChanged(theme => this.#card?.configureAvatar(avatar => avatar.setAvatar(this.#avatar(theme))));
        this.subscribe(() => onAssemblyUpdated(id, this.configuration.assembly.set));
    }

    key = () => this.properties.assembly.id;

    expanded = () => this.#card?.expanded();

    draw = () => this.#card?.render() || empty().render();
}

export const assemblyCard = (properties: Properties) => new AssemblyCard(properties);
