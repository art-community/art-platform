import {Widget} from "../../../framework/widgets/Widget";
import {dialog} from "../../../framework/dsl/managed/ManagedDialog";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {Closable} from "../../../framework/pattern/Optional";
import {moduleConfigurator} from "./configurator/ModuleConfigurator";
import {ResourcesStore} from "../../../loader/ResourcesLoader";
import {validateModuleConfiguration} from "../../../validator/ModuleConfigurationValidator";
import {DispatchWithoutAction} from "react";
import {useModuleApi} from "../../../api/ModuleApi";
import {Module, ModuleInformation} from "../../../model/ModuleTypes";
import {useFileApi} from "../../../api/FileApi";
import {moduleUpdater} from '../../../service/ModuleUpdatingService';
import {assembledArtifactSelector} from "../common/PlatformSelectors";
import {verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {AssembledArtifact} from "../../../model/AssemblyTypes";
import {Configurable} from "../../../framework/pattern/Configurable";
import {conditional} from "../../../framework/pattern/Conditional";
import {isNotEmptyArray} from "../../../framework/extensions/extensions";
import {useProjectApi} from "../../../api/ProjectApi";
import {ApplicationsStore} from "../../../loader/ApplicationsLoader";
import {PreparedConfigurationIdentifier} from "../../../model/PreparedConfigurationTypes";

type Properties = {
    resources: ResourcesStore
    applications: ApplicationsStore
    baseModule: Module
    modules: ModuleInformation[]
    preparedConfigurations: PreparedConfigurationIdentifier[]
}

class Configuration extends Configurable<Properties> {
    artifacts = this.property<AssembledArtifact[]>()
}

export class ModuleUpdatingDialog extends Widget<ModuleUpdatingDialog, Properties, Configuration> implements Closable {
    #projectApi = this.hookValue(useProjectApi)

    #moduleApi = this.hookValue(useModuleApi)

    #fileApi = this.hookValue(useFileApi)

    #updater = moduleUpdater(this.#fileApi, this.#moduleApi);

    #artifactSelector = conditional(() => isNotEmptyArray(this.configuration.artifacts.value))
    .persist(() => assembledArtifactSelector({
        artifacts: this.configuration.artifacts.value,
        selected: this.properties.baseModule.artifact,
        label: "Использовать"
    })
    .onSelect(() => this.#validate()));

    #moduleConfigurator = moduleConfigurator({
        disableNameEditing: true,
        applications: this.properties.applications,
        modules: this.properties.modules,
        artifact: this.properties.baseModule.artifact,
        baseModule: this.properties.baseModule,
        resources: this.properties.resources,
        preparedConfigurations: this.properties.preparedConfigurations,
        projectId: this.properties.baseModule.projectId
    })
    .onChange(() => this.#validate())

    #configurator = verticalGrid({spacing: 1, wrap: "nowrap"})
    .pushWidget(label({variant: "h6", color: "secondary", text: "Артефакт"}))
    .pushWidget(this.#artifactSelector)
    .pushWidget(this.#moduleConfigurator)

    #button = button({
        fullWidth: true,
        variant: "contained",
        color: "primary",
        label: "Изменить"
    })
    .onClick(() => this.#updater.update(this.properties.baseModule.projectId, this.properties.baseModule, {
        ...this.#moduleConfigurator.configure(),
        artifact: this.#artifactSelector.get()!.selected()
    }))
    .onClick(() => this.#dialog.close());

    #dialog = dialog({
        label: `Изменение модуля ${this.properties.baseModule.name}`,
        fullWidth: true,
        visible: true,
        disableEnforceFocus: true,
        maxWidth: "lg"
    })
    .widget(this.#configurator)
    .action(this.#button, {justify: "flex-end"});

    #validate = () => this.#button.setDisabled(!validateModuleConfiguration(
        this.#moduleConfigurator.configure(),
        this.properties.baseModule.projectId,
        this.properties.modules.filter(module => module.id != this.properties.baseModule.id)
    ));

    constructor(properties: Properties) {
        super(properties, Configuration);
        const request = {projectId: this.properties.baseModule.projectId};
        this.onLoad(() => this.#projectApi().getAssembledProjectArtifacts(request, artifacts => {
            this.configuration.artifacts.set(artifacts)
            this.#artifactSelector.notify();
        }))
    }

    onUpdate = (action: DispatchWithoutAction) => {
        this.#button.onClick(action)
        return this;
    }

    onClose = this.#dialog.onClose;

    draw = this.#dialog.render;
}

export const moduleUpdatingDialog = (properties: Properties) => new ModuleUpdatingDialog(properties);
