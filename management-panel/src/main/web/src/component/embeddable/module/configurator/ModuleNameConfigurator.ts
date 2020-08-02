import {Widget} from "../../../../framework/widgets/Widget";
import {text} from "../../../../framework/dsl/managed/ManagedTextField";
import {Configurator} from "../../../../framework/pattern/Configurator";
import {AssembledArtifact} from "../../../../model/AssemblyTypes";
import {DispatchWithoutAction} from "react";
import {verticalGrid} from "../../../../framework/dsl/managed/ManagedGrid";
import {label} from "../../../../framework/dsl/managed/ManagedLabel";

type Properties = {
    artifact: AssembledArtifact
    name?: string
    moduleNames: string[]
}

export class ModuleNameConfigurator extends Widget<ModuleNameConfigurator, Properties> implements Configurator<string> {
    #name = text({
        placeholder: `${this.properties.artifact.name}-${this.properties.artifact.version}`,
        defaultErrorText: "Имя модуля не должно быть пустым и содержать только символы [0-9a-z-.]. Максимальная длина - 63 символа.",
        required: true,
        value: this.properties.name || `${this.properties.artifact.name}-${this.properties.artifact.version}`,
        fullWidth: true
    })
    .onTextChanged(name => this.#validate(name));

    #validate = (name: string) => {
        if (!this.properties.moduleNames.has(name)) {
            return;
        }
        this.#name.setError({
            error: true,
            text: "У проекта уже есть модуль с таким именем, выберите другое"
        })
    }

    #configurator = verticalGrid({spacing: 1, wrap: "nowrap"})
    .pushWidget(label({color: "secondary", variant: "h6", noWrap: true, text: "Имя"}))
    .pushWidget(this.#name);

    constructor(properties: Properties) {
        super(properties);
        this.#validate(properties.name || `${this.properties.artifact.name}-${this.properties.artifact.version}`)
    }

    onChange = (action: DispatchWithoutAction) => {
        this.#name.onTextChanged(action)
        return this;
    }

    configure = () => this.#name.text();

    draw = this.#configurator.render;
}

export const moduleNameConfigurator = (properties: Properties) => new ModuleNameConfigurator(properties);
