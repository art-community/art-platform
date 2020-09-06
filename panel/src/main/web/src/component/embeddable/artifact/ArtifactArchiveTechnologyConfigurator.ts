import {DEFAULT_ARTIFACT_ARCHIVE_FORMAT, DOCKER} from "../../../constants/TechnologyConstants";
import {dockerConfigurator} from "../docker/DockerConfigurator";
import {Optional, optional} from "../../../framework/pattern/Optional";
import {group} from "../../../framework/dsl/simple/SimpleGroup";
import {Configurator} from "../../../framework/pattern/Configurator";
import {lazy} from "../../../framework/pattern/Lazy";
import {ArtifactArchiveConfiguration} from "../../../model/AssemblyTypes";
import {DispatchWithoutAction} from "react";
import {Widget} from "../../../framework/widgets/Widget";
import {Configurable} from "../../../framework/pattern/Configurable";
import {event} from "../../../framework/pattern/Event";

type Properties = {
    initialConfiguration?: ArtifactArchiveConfiguration
}

class Configuration extends Configurable<Properties> {
    change = event()
}

export class ArtifactArchiveTechnologyConfigurator extends Widget<ArtifactArchiveTechnologyConfigurator, Properties, Configuration> {
    #configurators = new Map<string, Optional<Configurator<unknown>>>()

    #docker = lazy(() => dockerConfigurator(this.properties.initialConfiguration?.dockerConfiguration)
    .onChange(this.configuration.change.execute))

    configureDocker = () => {
        if (this.#selectedFormat != DOCKER) {
            return undefined;
        }
        return this.#docker().configure()
    }

    #selectedFormat?: string;

    #select = (format: string) => {
        this.#selectedFormat = format;
        this.#configurators.forEach((optional, key) => key == format ? optional.spawn() : optional.destroy());
    };

    constructor(properties: Properties) {
        super(properties, Configuration)
        this.#configurators.set(DOCKER, optional(this.#docker).persist())
        this.selectFormat(this.properties.initialConfiguration?.archiveTechnology || DEFAULT_ARTIFACT_ARCHIVE_FORMAT);
    }

    selectFormat = (format: string) => {
        this.#select(format)
        return this;
    }

    onChange = (action: DispatchWithoutAction) => {
        this.configuration.change.handle(action)
        return this;
    }

    draw = () => group().widgets(this.#configurators.valuesToArray()).render()
}

export const artifactArchiveTechnologyConfigurator = (initialConfiguration?: ArtifactArchiveConfiguration) =>
    new ArtifactArchiveTechnologyConfigurator({initialConfiguration})
