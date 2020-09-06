import {DispatchWithoutAction} from "react";
import {Widget} from "../../../framework/widgets/Widget";
import {ArtifactArchiveConfiguration} from "../../../model/AssemblyTypes";
import {verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {ResourceIdentifier} from "../../../model/ResourceTypes";
import {artifactFormatsOf} from "../../../constants/TechnologyConstants";
import {technologySelector} from "../common/PlatformSelectors";
import {Configurable} from "../../../framework/pattern/Configurable";
import {artifactArchiveTechnologyConfigurator} from "./ArtifactArchiveTechnologyConfigurator";
import {lazy} from "../../../framework/pattern/Lazy";
import {Configurator} from "../../../framework/pattern/Configurator";
import {event} from "../../../framework/pattern/Event";
import {panel} from "../../../framework/dsl/managed/ManagedPanel";

type Properties = {
    resourceId: ResourceIdentifier
    archiveConfiguration?: ArtifactArchiveConfiguration
}

class Configuration extends Configurable<Properties> {
    change = event();
}

export class ArtifactArchiveConfigurator extends Widget<ArtifactArchiveConfigurator, Properties, Configuration> implements Configurator<ArtifactArchiveConfiguration> {
    #archiveTechnologyConfigurator = artifactArchiveTechnologyConfigurator(this.properties.archiveConfiguration)
    .onChange(this.configuration.change.execute);

    #archiveTechnologySelector = technologySelector({
        technologies: artifactFormatsOf(this.properties.resourceId),
        selected: this.properties.archiveConfiguration?.archiveTechnology,
        label: "Упаковать в"
    })
    .onSelect(technology => this.#selectTechnology(technology))
    .onSelect(this.configuration.change.execute);

    #configurator = lazy(() => panel(verticalGrid({spacing: 2, wrap: "nowrap"})
        .pushWidget(this.#archiveTechnologySelector)
        .pushWidget(this.#archiveTechnologyConfigurator), {
            label: `Для ${this.properties.resourceId.name}`,
            expanded: true
        })
    );

    #selectTechnology = technology => {
        this.#archiveTechnologyConfigurator.selectFormat(technology)
        this.#archiveTechnologyConfigurator.notify();
    }

    onChange = (action: DispatchWithoutAction) => {
        this.configuration.change.handle(action)
        return this;
    }

    configure = () => ({
        archiveTechnology: this.#archiveTechnologySelector.selected(),
        resourceId: this.properties.resourceId,
        dockerConfiguration: this.#archiveTechnologyConfigurator.configureDocker()
    });

    key = () => `${this.properties.resourceId.id}-${this.properties.resourceId.type}`;

    draw = () => this.#configurator().render();
}

export const artifactArchiveConfigurator = (properties: Properties) => new ArtifactArchiveConfigurator(properties, Configuration);
