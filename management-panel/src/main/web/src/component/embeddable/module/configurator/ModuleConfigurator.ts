import {moduleAdditionalFilesConfigurator} from "./ModuleAdditionalFilesConfigurator";
import {Module, ModuleConfigurationDraft, ModuleInformation} from "../../../../model/ModuleTypes";
import {Widget} from "../../../../framework/widgets/Widget";
import {ResourcesStore} from "../../../../loader/ResourcesLoader";
import {moduleNameConfigurator} from "./ModuleNameConfigurator";
import {AssembledArtifact} from "../../../../model/AssemblyTypes";
import {moduleParametersConfigurator} from "./ModuleParametersConfigurator";
import {moduleCountConfigurator} from "./ModuleCountConfigurator";
import {modulePortsConfigurator} from "./ModulePortsConfigurator";
import {moduleUrlConfigurator} from "./ModuleUrlConfigurator";
import {moduleConfigurationFilesConfigurator} from "./ModuleConfigurationFilesConfigurator";
import {Configurable} from "../../../../framework/pattern/Configurable";
import {conditional} from "../../../../framework/pattern/Conditional";
import {verticalGrid} from "../../../../framework/dsl/managed/ManagedGrid";
import {Configurator} from "../../../../framework/pattern/Configurator";
import {moduleResourceConfigurator} from "./ModuleResourceConfigurator";
import {DispatchWithoutAction} from "react";
import {moduleApplicationsCollection} from "../application/ModuleApplicationsCollection";
import {ApplicationsStore} from "../../../../loader/ApplicationsLoader";
import {PreparedConfigurationIdentifier} from "../../../../model/PreparedConfigurationTypes";

type Properties = {
    disableNameEditing?: boolean
    baseModule?: Module
    artifact: AssembledArtifact
    resources: ResourcesStore
    applications: ApplicationsStore
    preparedConfigurations: PreparedConfigurationIdentifier[]
    modules: ModuleInformation[]
    projectId: number
}

class Configuration extends Configurable<Properties> {
}

export class ModuleConfigurator extends Widget<ModuleConfigurator, Properties, Configuration> implements Configurator<ModuleConfigurationDraft> {
    #name = conditional(() => !this.properties.disableNameEditing)
    .persist(() => moduleNameConfigurator({
        artifact: this.properties.artifact,
        name: this.properties.baseModule?.name,
        moduleNames: this.properties.modules.map(module => module.name)
    }))

    #resource = moduleResourceConfigurator({
        resources: this.properties.resources,
        resourceId: this.properties.baseModule?.resourceId
    })

    #count = moduleCountConfigurator(this.properties.baseModule?.count)

    #parameters = moduleParametersConfigurator(this.properties.baseModule?.parameters)

    #url = moduleUrlConfigurator({
        artifact: this.properties.artifact,
        url: this.properties.baseModule?.url
    })

    #ports = modulePortsConfigurator({ports: this.properties.baseModule?.ports});

    #configurationFiles = moduleConfigurationFilesConfigurator({
        manualConfigurations: this.properties.baseModule?.manualConfigurations,
        preparedConfigurations: this.properties.baseModule?.preparedConfigurations,
        availablePreparedConfigurations: this.properties.preparedConfigurations,
        projectId: this.properties.projectId,
        thisConfigurator: this
    })

    #additionalFiles = moduleAdditionalFilesConfigurator(this.properties.baseModule?.additionalFiles?.map(file => file.name))

    #applications = moduleApplicationsCollection({
        moduleApplications: this.properties.baseModule?.applications,
        resourcesIds: this.properties.resources.ids,
        applications: this.properties.applications,
        thisConfigurator: this
    })

    #configurator = verticalGrid({spacing: 2})
    .pushWidget(this.#name)
    .pushWidget(this.#resource)
    .pushWidget(this.#count)
    .pushWidget(this.#parameters)
    .pushWidget(this.#ports)
    .pushWidget(this.#url)
    .pushWidget(this.#configurationFiles)
    .pushWidget(this.#additionalFiles)
    .pushWidget(this.#applications)

    configure = (): ModuleConfigurationDraft => ({
        name: this.#name.get()?.configure()
            || this.properties.baseModule?.name
            || `${this.properties.artifact.name}-${this.properties.artifact.version}`,
        resourceId: this.#resource.configure(),
        count: this.#count.configure(),
        artifact: this.properties.artifact,
        parameters: this.#parameters.configure(),
        ports: this.#ports.configure(),
        url: this.#url.configure(),
        manualConfigurations: this.#configurationFiles.configureManual(),
        preparedConfigurations: this.#configurationFiles.configurePrepared(),
        additionalFiles: this.#additionalFiles.configure(),
        applications: this.#applications.configure(),
        hasUrl: this.#url.checked(),
        hasParameters: this.#parameters.checked(),
        hasPorts: this.#ports.checked(),
        hasStringConfigurations: this.#configurationFiles.checked()
    })

    onChange = (action: DispatchWithoutAction) => {
        this.#name.apply(name => name.onChange(action))
        this.#resource.onChange(action)
        this.#count.onChange(action)
        this.#parameters.onChange(action)
        this.#ports.onChange(action)
        this.#url.onChange(action)
        this.#additionalFiles.onChange(action)
        this.#configurationFiles.onChange(action)
        this.#applications.onChange(action)
        return this;
    }

    key = () => this.properties.artifact.externalId.id

    draw = this.#configurator.render;
}

export const moduleConfigurator = (properties: Properties) => new ModuleConfigurator(properties, Configuration);
