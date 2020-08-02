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
import {assembledArtifactSelector} from "../common/PlatformSelectors";
import {verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {moduleCloner} from "../../../service/ModuleCloningService";
import {ApplicationsStore} from "../../../loader/ApplicationsLoader";
import {PreparedConfigurationIdentifier} from "../../../model/PreparedConfigurationTypes";
import {AssembledArtifact} from "../../../model/AssemblyTypes";
import {Configurable} from "../../../framework/pattern/Configurable";
import {conditional} from "../../../framework/pattern/Conditional";
import {isNotEmptyArray} from "../../../framework/extensions/extensions";
import {useProjectApi} from "../../../api/ProjectApi";

type Properties = {
    applications: ApplicationsStore
    resources: ResourcesStore
    baseModule: Module
    modules: ModuleInformation[]
    preparedConfigurations: PreparedConfigurationIdentifier[]
}

class Configuration extends Configurable<Properties> {
    artifacts = this.property<AssembledArtifact[]>([])
}

export class ModuleCloningDialog extends Widget<ModuleCloningDialog, Properties, Configuration> implements Closable {
    #moduleApi = this.hookValue(useModuleApi)

    #fileApi = this.hookValue(useFileApi)

    #projectApi = this.hookValue(useProjectApi)

    #cloner = moduleCloner(this.#fileApi, this.#moduleApi);

    #artifactSelector = conditional(() => isNotEmptyArray(this.configuration.artifacts.value))
    .persist(() => assembledArtifactSelector({
        artifacts: this.configuration.artifacts.value,
        selected: this.properties.baseModule.artifact,
        label: "Использовать"
    }));

    #moduleConfigurator = moduleConfigurator({
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
        label: "Установить",
        disabled: true
    })
    .onClick(() => this.#cloner.clone(this.properties.baseModule.projectId, this.properties.baseModule, {
        ...this.#moduleConfigurator.configure(),
        artifact: this.#artifactSelector.get()!.selected()
    }))
    .onClick(() => this.#dialog.close());

    #validate = () => this.#button.setDisabled(!validateModuleConfiguration(
        this.#moduleConfigurator.configure(),
        this.properties.baseModule.projectId,
        this.properties.modules
    ));

    #dialog = dialog({
        label: `Установка модуля на базе ${this.properties.baseModule.name}`,
        fullWidth: true,
        visible: true,
        disableEnforceFocus: true,
        maxWidth: "lg"
    })
    .widget(this.#configurator)
    .action(this.#button, {justify: "flex-end"});

    constructor(properties: Properties) {
        super(properties, Configuration);

        const request = {projectId: this.properties.baseModule.projectId};
        this.onLoad(() => this.#projectApi().getAssembledProjectArtifacts(request, artifacts => {
            this.configuration.artifacts.set(artifacts)
            this.#artifactSelector.notify()
        }))
    }

    onClone = (action: DispatchWithoutAction) => {
        this.#button.onClick(action)
        return this;
    }

    onClose = this.#dialog.onClose;

    draw = this.#dialog.render;
}

export const moduleCloningDialog = (properties: Properties) => new ModuleCloningDialog(properties);
