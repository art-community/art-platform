import {Widget} from "../../../framework/widgets/Widget";
import {verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {lazy} from "../../../framework/pattern/Lazy";
import {technologySelector} from "../common/PlatformSelectors";
import {DEFAULT_DOCKER_CONTAINER, DOCKER_CONTAINERS} from "../../../constants/TechnologyConstants";
import {Configurable} from "../../../framework/pattern/Configurable";
import {text} from "../../../framework/dsl/managed/ManagedTextField";
import {stringCollection} from "../common/PlatformCollections";
import {DOCKER_IMAGE_REGEX} from "../../../constants/Regexps";
import {event} from "../../../framework/pattern/Event";
import {DispatchWithoutAction} from "react";
import {Configurator} from "../../../framework/pattern/Configurator";

type Properties = {
    initialConfiguration?: DockerArchiveConfiguration
}

class Configuration extends Configurable<Properties> {
    change = event();
}

class DockerArtifactConfigurator extends Widget<DockerArtifactConfigurator, Properties, Configuration> implements Configurator<DockerArchiveConfiguration> {
    #technologySelector = technologySelector({
        technologies: DOCKER_CONTAINERS,
        selected: this.properties.initialConfiguration?.containerTechnology || DEFAULT_DOCKER_CONTAINER,
        label: "Тип контейнера"
    })
    .onSelect(this.configuration.change.execute);

    #image = text({
        label: "Ссылка на образ",
        placeholder: "nginx",
        fullWidth: true,
        value: this.properties.initialConfiguration?.image,
        defaultErrorText: "Ссылка должна соответствовать правилам Docker тега",
        required: true,
        regexp: DOCKER_IMAGE_REGEX
    })
    .onTextChanged(this.configuration.change.execute);

    #paths = stringCollection({
        direction: "column",
        labelDivider: true,
        duplicateMessage: "Не указывайте одинаковые пути",
        itemLabel: "Исходный путь",
        collectionLabel: "Добавьте файлы и папки",
        placeholder: "dist",
        strings: this.properties.initialConfiguration?.sourcePaths,
        decorator: field => field.onTextChanged(this.configuration.change.execute),
    })
    .onAdd(this.configuration.change.execute)
    .onDelete(this.configuration.change.execute);

    #configurator = lazy(() => verticalGrid({spacing: 1, wrap: "nowrap"})
        .pushWidget(this.#technologySelector)
        .pushWidget(this.#image)
        .pushWidget(this.#paths)
    );

    configure = () => ({
        containerTechnology: this.#technologySelector.selected(),
        image: this.#image.text(),
        sourcePaths: this.#paths.values()
    });

    onChange = (action: DispatchWithoutAction) => {
        this.configuration.change.handle(action)
        return this;
    }

    draw = () => this.#configurator().render()
}

export const dockerConfigurator = (initialConfiguration?: DockerArchiveConfiguration) => new DockerArtifactConfigurator({initialConfiguration}, Configuration);
