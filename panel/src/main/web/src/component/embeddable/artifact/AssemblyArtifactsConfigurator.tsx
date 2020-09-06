import {default as React, DispatchWithoutAction} from "react";
import {Widget} from "../../../framework/widgets/Widget";
import {gridItem, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {ProjectArtifact} from "../../../model/ProjectTypes";
import {ResourceIdentifier} from "../../../model/ResourceTypes";
import {proxy} from "../../../framework/widgets/Proxy";
import {group} from "../../../framework/dsl/simple/SimpleGroup";
import {ArtifactConfiguration} from "../../../model/AssemblyTypes";
import {Configurator} from "../../../framework/pattern/Configurator";
import {AssemblyArtifactGroup, assemblyArtifactsGroup} from "./AssemblyArtifactsGroup";
import {AssemblyTechnologyConfigurator} from "../assembly/AssemblyTechnologyConfigurator";
import {Configurable} from "../../../framework/pattern/Configurable";
import AddOutlined from "@material-ui/icons/AddOutlined";
import {event} from "../../../framework/pattern/Event";
import {artifactsGroupAdditionDialog} from "./ArtifactsGroupAdditionDialog";
import {panel} from "../../../framework/dsl/managed/ManagedPanel";
import {asynchronous, crossEquals} from "../../../framework/extensions/extensions";
import {optional} from '../../../framework/pattern/Optional';

interface Properties {
    resourceIds: ResourceIdentifier[]
    assemblyTechnologyConfigurator: AssemblyTechnologyConfigurator
    projectArtifacts: ProjectArtifact[]
    assemblyTechnology: string
    initialConfigurations?: ArtifactConfiguration[]
}

class Configuration extends Configurable<Properties> {
    change = event();
}

export class AssemblyArtifactsConfigurator extends Widget<AssemblyArtifactsConfigurator, Properties, Configuration> implements Configurator<ArtifactConfiguration[]> {
    #selectedArtifacts: ProjectArtifact[] = []

    #groups: Map<string, AssemblyArtifactGroup> = new Map();

    #groupsGrid = verticalGrid({spacing: 1});

    #addNewGroup = (name: string) => {
        const group = assemblyArtifactsGroup({
            name,
            availableArtifacts: this.properties.projectArtifacts.filter(artifact => !this.#selectedArtifacts.has(artifact)),
            assemblyTechnology: this.properties.assemblyTechnology,
            resourceIds: this.properties.resourceIds,
            assemblyTechnologyConfigurator: this.properties.assemblyTechnologyConfigurator
        })
        .onDelete(() => {
            this.#groups.delete(name)
            this.#refreshGroups()
        })
        .onSelect(() => this.#recalculateAvailableArtifacts(name))
        .onSelect(this.configuration.change.execute)
        .onChange(this.configuration.change.execute);

        this.#groups.set(name, group)
        this.#refreshGroups();
    };

    #addExistedGroup = (configuration: ArtifactConfiguration) => {
        const selectedArtifacts = this.properties.initialConfigurations
        ?.filter(initialConfiguration => initialConfiguration.name == configuration.name && initialConfiguration.artifact)
        ?.map(initialConfiguration => initialConfiguration.artifact!);
        const availableArtifacts = this.properties.projectArtifacts.filter(artifact => !this.#selectedArtifacts.has(artifact));
        const group = assemblyArtifactsGroup({
            name: configuration.name,
            availableArtifacts,
            selectedArtifacts,
            assemblyTechnology: this.properties.assemblyTechnology,
            resourceIds: this.properties.resourceIds,
            assemblyTechnologyConfigurator: this.properties.assemblyTechnologyConfigurator,
            initialConfiguration: configuration
        })
        .onDelete(() => {
            this.#groups.delete(configuration.name)
            this.#refreshGroups()
        })
        .onSelect(() => this.#recalculateAvailableArtifacts(configuration.name))
        .onSelect(this.configuration.change.execute)
        .onChange(this.configuration.change.execute);

        this.#groups.set(configuration.name, group)
        this.#refreshGroups();
    }

    #refreshGroups = () => {
        this.#groupsGrid.lock(() => {
            this.#groups.forEach((configurator, name) => {
                if (!this.#groupsGrid.hasKey(name)) {
                    this.#groupsGrid.pushItem(gridItem(panel(configurator, {label: name, expanded: true}), name))
                }
            })
            this.#groupsGrid.keys().forEach((name: string) => {
                if (!this.#groups.has(name)) {
                    this.#groupsGrid.removeKey(name)
                }
            })
            this.#recalculateAvailableArtifacts()
            this.configuration.change.execute()
        })
    }

    #recalculateAvailableArtifacts = (updatedGroupName?: string) => {
        const newSelectedArtifacts = this.#groups
        .valuesToArray(group => group.selectedArtifacts())
        .flatMap(artifacts => artifacts.unique(artifact => artifact.name))

        if (crossEquals(newSelectedArtifacts, this.#selectedArtifacts)) {
            return
        }

        this.#selectedArtifacts = newSelectedArtifacts;

        asynchronous(() => this.#groups.forEach((group, name) => {
            if (updatedGroupName != name) {
                const availableArtifacts = this.properties.projectArtifacts.filter(artifact => !this.#selectedArtifacts.has(artifact));
                group.setAvailableArtifacts(availableArtifacts)
            }
        }))
    };

    #groupDialog = this.add(optional(() => artifactsGroupAdditionDialog(Array.from(this.#groups.keys()))
    .onAdd(this.#addNewGroup)))

    #assemblyArtifacts = group()
    .widget(verticalGrid({spacing: 1, wrap: "nowrap"})
        .pushWidget(this.#groupsGrid)
        .pushWidget(button({
            icon: proxy(<AddOutlined color={"secondary"}/>),
            tooltip: "Добавить артефакты"
        })
        .onClick(this.#groupDialog.spawn))
    )

    constructor(properties: Properties) {
        super(properties, Configuration);
        this.properties.initialConfigurations
        ?.unique(configuration => configuration.name)
        ?.forEach(this.#addExistedGroup);
    }

    onChange = (action: DispatchWithoutAction) => {
        this.configuration.change.handle(action)
        return this;
    }

    configure = (): ArtifactConfiguration[] => this.#groups.valuesToArray(configurator => configurator.configure())
    .flatMap(configuration => configuration);

    draw = this.#assemblyArtifacts.render;
}

export const assemblyArtifactsConfigurator = (properties: Properties) => new AssemblyArtifactsConfigurator(properties);
