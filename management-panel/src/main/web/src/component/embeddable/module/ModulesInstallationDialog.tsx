import {Project} from "../../../model/ProjectTypes";
import {AssembledArtifact} from "../../../model/AssemblyTypes";
import {Widget} from "../../../framework/widgets/Widget";
import {dialog} from "../../../framework/dsl/managed/ManagedDialog";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {conditional} from "../../../framework/pattern/Conditional";
import {assembledArtifactsSearcher} from "../common/PlatformSearchers";
import {Closable} from "../../../framework/pattern/Optional";
import {group} from "../../../framework/dsl/simple/SimpleGroup";
import {divider} from "../../../framework/dsl/simple/SimpleDivider";
import {gridItem, horizontalGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {Configurable} from "../../../framework/pattern/Configurable";
import {projectSelector} from "../common/PlatformSelectors";
import {useProjectApi} from "../../../api/ProjectApi";
import {isNotEmptyArray} from "../../../framework/extensions/extensions";
import {ModuleConfigurator, moduleConfigurator} from "./configurator/ModuleConfigurator";
import {ResourcesStore} from "../../../loader/ResourcesLoader";
import {panel} from "../../../framework/dsl/managed/ManagedPanel";
import {validateModuleConfiguration} from "../../../validator/ModuleConfigurationValidator";
import React, {DispatchWithoutAction} from "react";
import {useModuleApi} from "../../../api/ModuleApi";
import {ModuleInformation} from "../../../model/ModuleTypes";
import {useFileApi} from "../../../api/FileApi";
import {moduleInstaller} from "../../../service/ModuleInstallationService";
import {proxy} from "../../../framework/widgets/Proxy";
import SelectAllOutlined from "@material-ui/icons/SelectAllOutlined";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {ApplicationsStore} from "../../../loader/ApplicationsLoader";
import {PreparedConfigurationIdentifier} from "../../../model/PreparedConfigurationTypes";

type Properties = {
    project?: Project
    artifacts?: AssembledArtifact[]
    projects: Project[]
    resources: ResourcesStore
    applications: ApplicationsStore
    preparedConfigurations: PreparedConfigurationIdentifier[]
}

class Configuration extends Configurable<Properties> {
    artifacts = this.property(this.defaultProperties.artifacts)

    modules = this.property<ModuleInformation[]>([])
}

export class ModulesInstallationDialog extends Widget<ModulesInstallationDialog, Properties, Configuration> implements Closable {
    #configurators = new Map<string, ModuleConfigurator>()

    #projectApi = this.hookValue(useProjectApi)

    #moduleApi = this.hookValue(useModuleApi)

    #fileApi = this.hookValue(useFileApi)

    #installer = moduleInstaller(this.#fileApi, this.#moduleApi);

    #projectSelector = projectSelector({projects: this.properties.projects});

    #artifactsSearcher = assembledArtifactsSearcher({artifacts: this.configuration.artifacts.value || []})
    .apply(searcher => searcher.onSelect(this.#selectArtifacts))

    #modules = verticalGrid({spacing: 1, wrap: "nowrap"})

    #configurator = verticalGrid({spacing: 1, wrap: "nowrap"})
    .pushWidget(conditional(() => !this.properties.project).persist(() => group()
        .widget(this.#projectSelector.onSelect(this.#reloadArtifacts))
        .widget(divider())
    ))

    .pushWidget(conditional(() => isNotEmptyArray(this.configuration.artifacts.value))
    .persist(() => horizontalGrid({spacing: 1, wrap: "nowrap", alignItems: "center"})
        .pushWidget(this.#artifactsSearcher, {xs: true})
        .pushWidget(button({icon: proxy(<SelectAllOutlined color={"secondary"}/>), tooltip: "Выбрать все"})
            .onClick(() => this.#artifactsSearcher.get().select(this.configuration.artifacts.value || []))
        )
    )
    .else(label({variant: "h6", color: "secondary", text: "Артефакты отсутствуют"})))

    .pushWidget(this.#modules)

    #button = button({
        fullWidth: true,
        variant: "contained",
        color: "primary",
        label: "Установить",
        disabled: true
    })
    .onClick(() => this.#installer.install(this.#project().id, this.#configurators.valuesToArray(configurator => configurator.configure())))
    .onClick(() => this.#dialog.close());

    #dialog = dialog({
        label: "Установка модулей",
        fullWidth: true,
        visible: true,
        disableEscapeKeyDown: true,
        disableEnforceFocus: true,
        maxWidth: "lg"
    })
    .widget(this.#configurator)
    .action(this.#button, {justify: "flex-end"});

    #project = () => this.properties.project || this.#projectSelector.selected()

    #validate = (configurator: ModuleConfigurator) => validateModuleConfiguration(
        configurator.configure(),
        this.#project().id,
        this.configuration.modules.value
    );

    #reloadArtifacts = (project: Project) => this.#projectApi().getAssembledProjectArtifacts({projectId: project.id}, artifacts => {
        const unique = artifacts.unique(artifact => artifact.name)
        this.configuration.artifacts.set(unique)
        this.#artifactsSearcher.apply(searcher => searcher.setAvailableValues(unique))
        this.#configurators.clear()
        this.#modules.clear()
        this.#configurator.notify()
    })

    #selectArtifacts = (artifacts: AssembledArtifact[]) => {
        this.#modules.lock(() => {
            artifacts.forEach(artifact => {
                if (!this.#modules.hasKey(artifact.name)) {
                    const configurator = moduleConfigurator({
                        artifact,
                        applications: this.properties.applications,
                        resources: this.properties.resources,
                        modules: this.configuration.modules.value!.filter(module => module.projectId == this.#projectSelector.selected().id),
                        preparedConfigurations: this.properties.preparedConfigurations,
                        projectId: this.#project().id
                    })
                    .onChange(() => this.#button.setDisabled(!this.#validate(configurator)));
                    this.#modules.pushWidget(gridItem(panel(configurator, {label: artifact.name, labelColor: "primary"}), artifact.name))
                    this.#configurators.set(artifact.name, configurator)
                }
            })
            this.#modules.widgets().forEach(widget => {
                if (!artifacts.some(artifact => artifact.name == widget.key())) {
                    this.#modules.removeWidget(widget)
                    this.#configurators.delete(widget.key() as string)
                }
            })
        })
        this.#validateConfigurations()
    }

    #validateConfigurations = () => {
        if (this.#configurators.isEmpty()) {
            this.#button.setDisabled(true)
            return;
        }
        if (this.#configurators.some((name, configurator) => !this.#validate(configurator))) {
            this.#button.setDisabled(true);
            return;
        }
        this.#button.setDisabled(false);
    }

    constructor(properties: Properties) {
        super(properties, Configuration);
        this.onLoad(() => {
            this.#moduleApi().getFilteredModules({sorted: true}, this.configuration.modules.set)
            if (!properties.artifacts) {
                this.#reloadArtifacts(properties.project || properties.projects[0])
            }
        })
    }

    onInstall = (action: DispatchWithoutAction) => {
        this.#button.onClick(action)
        return this;
    }

    onClose = this.#dialog.onClose;

    draw = this.#dialog.render;
}

export const modulesInstallationDialog = (properties: Properties) => new ModulesInstallationDialog(properties);
