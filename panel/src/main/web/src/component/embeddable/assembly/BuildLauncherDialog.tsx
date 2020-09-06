import {Project, ProjectArtifact} from "../../../model/ProjectTypes";
import {Widget} from "../../../framework/widgets/Widget";
import {conditional} from "../../../framework/pattern/Conditional";
import {projectSelector, resourceSelector, stringSelector} from "../common/PlatformSelectors";
import {dialog} from "../../../framework/dsl/managed/ManagedDialog";
import {Closable} from "../../../framework/pattern/Optional";
import {Configurable} from "../../../framework/pattern/Configurable";
import {ResourcesStore} from "../../../loader/ResourcesLoader";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {EXECUTORS} from "../../../constants/ResourceConstants";
import {projectArtifactsSearcher} from "../common/PlatformSearchers";
import {AssemblyConfiguration} from "../../../model/AssemblyTypes";
import {useAssemblyApi} from "../../../api/AssemblyApi";
import {validateAssemblyConfiguration} from "../../../validator/AssemblyConfigurationValidator";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import React, {DispatchWithoutAction} from "react";
import {event} from "../../../framework/pattern/Event";
import {horizontalGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {proxy} from "../../../framework/widgets/Proxy";
import SelectAllOutlined from "@material-ui/icons/SelectAllOutlined";
import {isEmptyArray} from "../../../framework/extensions/extensions";
import {lazy} from "../../../framework/pattern/Lazy";
import {magicLoader} from "../common/PlatformLoaders";

type Properties = {
    project?: Project
    projects: Project[]
    resources: ResourcesStore
}

class Configuration extends Configurable<Properties> {
    project = this.property(this.defaultProperties.project || this.defaultProperties.projects[0])

    assemblyConfiguration = this.property<AssemblyConfiguration>()

    launch = event();
}

export class BuildLauncherDialog extends Widget<BuildLauncherDialog, Properties, Configuration> implements Closable {
    #api = this.hookValue(useAssemblyApi)

    #configurationInvalidLabel = label({
        color: "secondary",
        variant: "h6",
        text: "Сборка проекта не настроена"
    });

    #reloadAssemblyConfiguration = () => {
        const id = this.configuration.project.value!.id;
        this.#api().getAssemblyConfiguration(id, configuration => {
            this.configuration.assemblyConfiguration.set(configuration)
            this.#launcher.notify()
            this.#button.notify()
        })
    }

    #initialized = () => this.configuration.assemblyConfiguration.value

    #hasValidConfiguration = () => this.#initialized() && validateAssemblyConfiguration(this.configuration.assemblyConfiguration.value)

    #getAvailableArtifacts = (version: string): ProjectArtifact[] => {
        const artifacts = this.configuration.project.value!.artifacts
        .filter(artifact => artifact.versions.some(artifactVersion => artifactVersion.version == version));

        if (isEmptyArray(artifacts)) {
            return [];
        }

        return artifacts.flatMap(artifact => this.configuration.assemblyConfiguration.value!.artifactConfigurations
        ?.map(configuration => configuration.artifact)
        ?.filter(configurationArtifact => configurationArtifact && configurationArtifact.name == artifact.name) || [])  as ProjectArtifact[];
    }

    #launchBuild = () => {
        const artifactConfigurations = this.configuration.assemblyConfiguration.value!.artifactConfigurations!
        .filter(configuration => this.#artifactsSearcher().get().selected().has(configuration.artifact!));

        const request = {
            projectId: this.configuration.project.value!.id,
            configurationId: this.configuration.assemblyConfiguration.value!.id,
            version: this.configuration.project.value!.versions.find(version => version.version == this.#versionSelector().selected())!,
            resourceId: this.#resourceSelector.selected(),
            artifactConfigurations
        }
        this.#api().buildProject(request, this.configuration.launch.execute);
        this.#dialog.close();
    }

    #projectSelector = conditional(() => !this.properties.project)
    .persist(() => projectSelector({projects: this.properties.projects})
    .onSelect(this.configuration.project.set)
    .onSelect(project => this.#versionSelector().setAvailableValues(project.versions.map(version => version.version)))
    .onSelect(this.#reloadAssemblyConfiguration))

    #versionSelector = lazy(() => stringSelector({
        strings: this.configuration.project.value!.versions.map(version => version.version),
        label: "Версия"
    })
    .onSelect(version => this.#artifactsSearcher().get().setAvailableValues(this.#getAvailableArtifacts(version))));

    #resourceSelector = resourceSelector({
        ids: this.properties.resources.idsOf(EXECUTORS),
        label: "Ресурс для сборки",
        selected: this.configuration.assemblyConfiguration.value?.defaultResourceId
    });

    #artifactsSearcher = lazy(() => projectArtifactsSearcher({
        artifacts: this.#getAvailableArtifacts(this.#versionSelector().selected())
    }))

    #launcher = conditional(this.#initialized).persist(() => conditional(this.#hasValidConfiguration)
        .persist(() => verticalGrid({spacing: 2, wrap: "nowrap"})
            .pushWidget(this.#projectSelector)
            .pushWidget(this.#versionSelector())
            .pushWidget(this.#resourceSelector)
            .pushWidget(horizontalGrid({spacing: 1, wrap: "nowrap", alignItems: "center"})
            .pushWidget(this.#artifactsSearcher(), {xs: true})
            .pushWidget(button({
                icon: proxy(<SelectAllOutlined color={"secondary"}/>),
                tooltip: "Выбрать все"
            })
            .onClick(() => this.#artifactsSearcher().get()
                .select(this.#getAvailableArtifacts(this.#versionSelector().selected()))
            )))
        )
        .else(this.#configurationInvalidLabel)
    )
    .else(magicLoader(true));

    #button = conditional(this.#hasValidConfiguration).persist(() => button({
        fullWidth: true,
        color: "primary",
        variant: "contained",
        label: "Собрать"
    })
    .onClick(this.#launchBuild));

    #dialog = dialog({
        label: this.properties.project ? `Сборка проекта ${this.properties.project?.name}` : `Сборка`,
        maxWidth: "lg",
        fullWidth: true,
        visible: true
    })
    .widget(this.#launcher)
    .action(this.#button, {justify: "flex-end"});

    constructor(properties: Properties) {
        super(properties, Configuration);
        this.onLoad(this.#reloadAssemblyConfiguration);
    }

    onClose = this.#dialog.onClose;

    onLaunch = (action: DispatchWithoutAction) => {
        this.configuration.launch.handle(action)
        return this;
    }

    draw = this.#dialog.render;
}

export const buildLauncherDialog = (properties: Properties) => new BuildLauncherDialog(properties);
