import {Widget} from "../../../framework/widgets/Widget";
import {text} from "../../../framework/dsl/managed/ManagedTextField";
import {lazy} from "../../../framework/pattern/Lazy";
import {Configurator} from "../../../framework/pattern/Configurator";
import {GradleArtifactConfiguration} from "../../../model/GradleTypes";
import {event} from "../../../framework/pattern/Event";
import {DispatchWithoutAction} from "react";
import {Configurable} from "../../../framework/pattern/Configurable";

type Properties = {
    configuration?: GradleArtifactConfiguration
}

class Configuration extends Configurable<Properties> {
    change = event()
}

class GradleArtifactConfigurator extends Widget<GradleArtifactConfigurator, Properties, Configuration> implements Configurator<GradleArtifactConfiguration> {
    #arguments = text({
        fullWidth: true,
        placeholder: "buildJar",
        label: "Gradle аргументы",
        value: this.properties.configuration?.arguments
    })
    .onTextChanged(this.configuration.change.execute);

    #configurator = lazy(() => this.#arguments);

    configure = () => ({arguments: this.#arguments.text()})

    onChange = (action: DispatchWithoutAction) => {
        this.configuration.change.handle(action)
        return this;
    }

    draw = () => this.#configurator().render();
}

export const gradleArtifactConfigurator = (configuration?: GradleArtifactConfiguration) => new GradleArtifactConfigurator({configuration}, Configuration);
