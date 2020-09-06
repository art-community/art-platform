import * as React from "react";
import DeleteOutlined from "@material-ui/icons/DeleteOutlined";
import SettingsBackupRestoreOutlined from "@material-ui/icons/SettingsBackupRestoreOutlined";
import SettingsOutlined from "@material-ui/icons/SettingsOutlined";
import EditOutlined from "@material-ui/icons/EditOutlined";
import CancelOutlined from "@material-ui/icons/CancelOutlined";
import BuildOutlined from "@material-ui/icons/BuildOutlined";
import moment from "moment"
import {PROJECT_CREATED_STATE, PROJECT_INITIALIZATION_FAILED_STATE, PROJECT_RELOAD_STARTED_STATE, PROJECT_RELOADING_STATE} from "../../../constants/States";
import {Configurable} from "../../../framework/pattern/Configurable";
import {card} from "../../../framework/dsl/managed/card/ManagedCard";
import {proxy} from "../../../framework/widgets/Proxy";
import {CardMenuButton, CardMenuProperties} from "../../../framework/dsl/managed/card/CardMenu";
import {CardAttributeProperties} from "../../../framework/dsl/managed/card/CardAttribute";
import {gitResourceIcon, resourceIcon} from "../icon/ResourceIcon";
import {versionIcons} from "../icon/VersionIcons";
import {technologyIcons} from "../icon/TechnologyIcons";
import {platform} from "../../entry/EntryPoint";
import {onProjectUpdated} from "../../../streams/ProjectStream";
import {Project} from "../../../model/ProjectTypes";
import {isNotEmptyArray} from "../../../framework/extensions/extensions";
import {warning} from "../../../framework/dsl/managed/ManagedDialog";
import {useProjectApi} from "../../../api/ProjectApi";
import {projectEditingDialog} from "./ProjectEditingDialog";
import {arrayCache} from "../../../framework/pattern/ArrayCache";
import {optional} from "../../../framework/pattern/Optional";
import {Widget} from "../../../framework/widgets/Widget";
import {ResourcesStore} from "../../../loader/ResourcesLoader";
import {assemblyConfiguratorDialog} from '../assembly/AssemblyConfiguratorDialog';
import {useAssemblyApi} from "../../../api/AssemblyApi";
import {validateAssemblyConfiguration} from "../../../validator/AssemblyConfigurationValidator";
import {buildLauncherDialog} from '../assembly/BuildLauncherDialog';
import {ASSEMBLIES_PATH} from "../../../constants/Routers";
import {DATE_TIME_FORMAT} from "../../../constants/DateTimeConstants";

type Properties = {
    project: Project
    projects: Project[]
    resources: ResourcesStore
}

class Configuration extends Configurable<Properties> {
    project = this.property(this.defaultProperties.project);

    buildEnabled = this.property(false);
}

class ProjectCard extends Widget<ProjectCard, Properties, Configuration> {
    #projectApi = this.hookValue(useProjectApi);

    #assemblyApi = this.hookValue(useAssemblyApi);

    #checkBuildEnabled = () => this.#assemblyApi()
    .getAssemblyConfiguration(
        this.properties.project.id,
        configuration => this.configuration.buildEnabled.set(validateAssemblyConfiguration(configuration))
    )

    #versionsCache = arrayCache(
        () => this.configuration.project.value!.versions.map(version => version.version),
        versionIcons
    )

    #technologiesCache = arrayCache(
        () => this.configuration.project.value!.technologies,
        technologies => technologyIcons(technologies, true)
    )

    #avatar = () => {
        const project = this.configuration.project.value!;
        const failedAvatar = {icon: proxy(<CancelOutlined htmlColor={"red"}/>)};
        const normalAvatar = {letter: {firstLetter: project.name[0]}};
        return project.state == PROJECT_INITIALIZATION_FAILED_STATE ? failedAvatar : normalAvatar
    };

    #menu = (): CardMenuProperties => {
        const project = this.configuration.project.value!;
        const state = project.state;
        switch (state) {
            case PROJECT_CREATED_STATE:
            case PROJECT_RELOAD_STARTED_STATE:
            case PROJECT_RELOADING_STATE:
                return {indicator: {progress: true}};
        }

        const buttons: CardMenuButton[] = [];

        if (state == PROJECT_INITIALIZATION_FAILED_STATE) {
            buttons
            .with({
                tooltip: "Обновить",
                icon: proxy(<SettingsBackupRestoreOutlined color={"primary"}/>),
                onClick: () => this.#projectApi().reloadProject(this.properties.project.id)
            })
            .with({
                tooltip: "Удалить",
                icon: proxy(<DeleteOutlined color={"primary"}/>),
                onClick: this.#deletingDialog.open
            });

            return {actions: {buttons: buttons}};
        }

        buttons
        .with({
            tooltip: "Удалить",
            icon: proxy(<DeleteOutlined color={"primary"}/>),
            onClick: this.#deletingDialog.open
        })
        .with({
            tooltip: "Изменить",
            icon: proxy(<EditOutlined color={"primary"}/>),
            onClick: () => this.#editingDialog.spawn()
        })
        .with({
            tooltip: "Обновить",
            icon: proxy(<SettingsBackupRestoreOutlined color={"primary"}/>),
            onClick: () => this.#projectApi().reloadProject(this.properties.project.id)
        })
        .with({
            tooltip: "Настроить сборку",
            icon: proxy(<SettingsOutlined color={"primary"}/>),
            onClick: () => this.#assemblyConfiguratorDialog.spawn()
        });

        if (this.configuration.buildEnabled.value) {
            buttons.push({
                tooltip: "Собрать",
                icon: proxy(<BuildOutlined color={"primary"}/>),
                onClick: () => this.#buildLauncherDialog.spawn()
            });
        }

        return {actions: {buttons: buttons}};
    };

    #attributes = () => {
        const {
            creationTimeStamp,
            externalId,
            gitResourceId,
            technologies,
            versions
        } = this.configuration.project.value!;
        const attributes: CardAttributeProperties[] = [];

        attributes
        .with({name: "Метка создания", value: moment.unix(creationTimeStamp).format(DATE_TIME_FORMAT)})
        .with({name: "Git", icon: gitResourceIcon({name: gitResourceId.name})})
        .with({
            name: "Имя на внешнем ресурсе",
            value: externalId.id
        })
        .with({
            name: "Внешний ресурс",
            icon: resourceIcon({name: externalId.resourceId.name, type: externalId.resourceId.type})
        })

        if (isNotEmptyArray(versions)) {
            attributes.push({name: "Версии", icon: this.#versionsCache()});
        }

        if (isNotEmptyArray(technologies)) {
            attributes.push({name: "Технологии", icon: this.#technologiesCache()});
        }

        return attributes;
    };

    #deletingDialog = this.add(warning({
        label: "Вы уверены, что хотите удалить проект?",
        warning: "Удаление проекта повлечет за собой удаление всех сборок и модулей",
        cancelLabel: "Нет, оставить",
        approveLabel: "Да, удалить",
        onCancel: () => this.#deletingDialog.close(),
        onApprove: () => this.#projectApi().deleteProject(this.properties.project.id)
    }));

    #editingDialog = this.add(optional(() => projectEditingDialog(this.properties.resources, this.configuration.project.value)));

    #assemblyConfiguratorDialog = this.add(optional(() => assemblyConfiguratorDialog({
            project: this.configuration.project.value,
            resources: this.properties.resources
        }))
        .onDestroy(this.#checkBuildEnabled)
    );

    #buildLauncherDialog = this.add(optional(() => buildLauncherDialog({
            project: this.configuration.project.value,
            projects: this.properties.projects,
            resources: this.properties.resources
        })
        .onLaunch(() => platform.redirect(ASSEMBLIES_PATH)))
    );

    #card = card({label: this.configuration.project.value!.name})
    .configureAvatar(avatar => avatar.setAvatar(this.#avatar()))
    .configureMenu(menu => menu.setMenu(this.#menu()))
    .setAttributes(this.#attributes());

    constructor(properties: Properties) {
        super(properties, Configuration);
        const id = this.properties.project.id;
        this.widgetName = `(${this.constructor.name}): ${id}`
        this.configuration.buildEnabled.consume(() => this.#card.configureMenu(menu => menu.setMenu(this.#menu())));
        this.configuration.project.consume(project => {
            this.#card
            .setLabel(project!.name)
            .configureAvatar(avatar => avatar.setAvatar(this.#avatar()))
            .configureMenu(menu => menu.setMenu(this.#menu()))
            .setAttributes(this.#attributes())
        })
        this.subscribe(() => onProjectUpdated(id, this.configuration.project.set));
        this.onLoad(this.#checkBuildEnabled)
    }

    key = () => this.properties.project.id;

    draw = this.#card.render;
}

export const projectCard = (project: Project, projects: Project[], resources: ResourcesStore) => new ProjectCard({project, projects, resources});
