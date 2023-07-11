import * as React from "react";
import {DispatchWithoutAction} from "react";
import {Widget} from "../../../framework/widgets/Widget";
import {ArtifactArchiveConfiguration, ArtifactConfiguration} from "../../../model/AssemblyTypes";
import {horizontalGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {ResourceIdentifier} from "../../../model/ResourceTypes";
import {projectArtifactsSearcher, resourceSearcher} from "../common/PlatformSearchers";
import {GRADLE} from "../../../constants/TechnologyConstants";
import {Configurable} from "../../../framework/pattern/Configurable";
import {isEmptyArray} from "../../../framework/extensions/extensions";
import {lazy} from "../../../framework/pattern/Lazy";
import {Configurator} from "../../../framework/pattern/Configurator";
import {ProjectArtifact} from "../../../model/ProjectTypes";
import {AssemblyTechnologyConfigurator} from "../assembly/AssemblyTechnologyConfigurator";
import {event} from "../../../framework/pattern/Event";
import {when} from "../../../framework/pattern/When";
import {gradleArtifactConfigurator} from "../gradle/GradleArtifactConfigurator";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import DeleteOutlined from "@material-ui/icons/DeleteOutlined";
import {proxy} from "../../../framework/widgets/Proxy";
import {artifactArchiveConfigurator} from "./ArtifactArchiveConfigurator";


type Properties = {
    name: string
    availableArtifacts: ProjectArtifact[]
    selectedArtifacts?: ProjectArtifact[]
    assemblyTechnology: string
    resourceIds: ResourceIdentifier[]
    assemblyTechnologyConfigurator: AssemblyTechnologyConfigurator
    initialConfiguration?: ArtifactConfiguration
}

class Configuration extends Configurable<Properties> {
    change = event();

    delete = event();

    select = event();
}

export class AssemblyArtifactGroup extends Widget<AssemblyArtifactGroup, Properties, Configuration> implements Configurator<ArtifactConfiguration[]> {
    #artifactArchiveConfigurator = (resourceId: ResourceIdentifier, archive?: ArtifactArchiveConfiguration) => artifactArchiveConfigurator({
        resourceId,
        archiveConfiguration: archive
    })
    .onChange(this.configuration.change.execute)

    #refreshArchiveConfigurators = (resourceIds: ResourceIdentifier[]) => {
        this.#archives.lock(() => {
            const keys = resourceIds.map(id => `${id.id}-${id.type}`);
            keys.forEach((key, index) => {
                if (!this.#archives.hasKey(key)) {
                    this.#archives.pushWidget(this.#artifactArchiveConfigurator(resourceIds[index]))
                }
            })
            this.#archives.keys().forEach(key => {
                if (!keys.has(key as string)) {
                    this.#archives.removeKey(key)
                }
            })
        })
    }

    #artifactSearcher = projectArtifactsSearcher({
        artifacts: this.properties.availableArtifacts,
        disableCloseOnSelect: true,
        selected: this.properties.selectedArtifacts,
        label: "Применить к"
    })
    .apply(searcher => searcher.onSelect(this.configuration.select.execute));

    #resourceSearcher = resourceSearcher({
        ids: this.properties.resourceIds,
        selected: this.properties.initialConfiguration?.archives?.map(archive => archive.resourceId),
        label: "Хранить в"
    })
    .apply(searcher => searcher.onSelect(this.#refreshArchiveConfigurators))
    .apply(searcher => searcher.onSelect(this.configuration.change.execute));

    #gradleConfigurator = gradleArtifactConfigurator(this.properties.initialConfiguration?.gradleConfiguration)
    .onChange(this.configuration.change.execute);

    #deleteButton = button({
        icon: proxy(<DeleteOutlined color={"primary"}/>),
        tooltip: "Удалить"
    })
    .onClick(this.configuration.delete.execute)

    #archives = verticalGrid({spacing: 1, wrap: "nowrap"})
    .pushWidgets(this.properties.initialConfiguration?.archives.map(archive => this.#artifactArchiveConfigurator(archive.resourceId, archive)) || [])

    #configurator = lazy(() => horizontalGrid({alignItems: "flex-end"})
        .pushWidget(verticalGrid({spacing: 2, wrap: "nowrap"})
            .pushWidget(this.#artifactSearcher)
            .pushWidget(verticalGrid({spacing: 2, wrap: "nowrap"})
                .pushWidget(when()
                    .persist(() => this.properties.assemblyTechnology == GRADLE, () => this.#gradleConfigurator)
                )
                .pushWidget(this.#resourceSearcher)
                .pushWidget(this.#archives)
            ), {xs: true}
        )
        .pushWidget(this.#deleteButton)
    )

    selectedArtifacts = () => this.#artifactSearcher.evaluated()
        ? this.#artifactSearcher.get().selected()
        : [];

    setAvailableArtifacts = (artifacts: ProjectArtifact[]) => {
        if (isEmptyArray(artifacts)) {
            this.#artifactSearcher.get().setAvailableValues([...this.#artifactSearcher.get().selected() || []]);
            return this;
        }
        const selectedIndexes = this.#artifactSearcher.get().selectedIndexes() || [];
        const currentAvailable = this.#artifactSearcher.get().availableValues() || [];
        const newAvailable: ProjectArtifact[] = [];
        selectedIndexes.forEach(index => newAvailable[index] = currentAvailable[index]);
        artifacts.forEach((artifact, index) => {
            if (!newAvailable[index]) {
                newAvailable[index] = artifact
            }
        })
        this.#artifactSearcher.get().setAvailableValues(newAvailable.filter(artifact => artifact != undefined));
        return this;
    }

    onSelect = (action: DispatchWithoutAction) => {
        this.configuration.select.handle(action)
        return this;
    }

    onChange = (action: DispatchWithoutAction) => {
        this.configuration.change.handle(action)
        return this;
    }

    onDelete = (action: DispatchWithoutAction) => {
        this.configuration.delete.handle(action)
        return this;
    }

    configure = () => this.#artifactSearcher.evaluated()
        ? this.#artifactSearcher.get().selected().map(artifact => ({
            artifact,
            name: this.properties.name,
            archives: this.#archives.widgets().map((configurator: Configurator<ArtifactArchiveConfiguration>) => configurator.configure()),
            gradleConfiguration: this.#gradleConfigurator.configure(),
        }))
        : []

    draw = () => this.#configurator().render();
}

export const assemblyArtifactsGroup = (properties: Properties) => new AssemblyArtifactGroup(properties, Configuration);
