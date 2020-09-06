import {Widget} from "../../../../framework/widgets/Widget";
import {checkBoxPanel} from "../../../../framework/dsl/managed/ManagedPanel";
import {text} from "../../../../framework/dsl/managed/ManagedTextField";
import {Configurator} from "../../../../framework/pattern/Configurator";
import {DispatchWithoutAction} from "react";

type Properties = {
    parameters?: string
}

export class ModuleParametersConfigurator extends Widget<ModuleParametersConfigurator, Properties> implements Configurator<string> {
    #parameters = text({
        label: "Введите параметры",
        placeholder: "-Dversion=1.0",
        required: true,
        value: this.properties.parameters,
        fullWidth: true
    });

    #configurator = checkBoxPanel(this.#parameters, {label: "Параметры запуска", checked: Boolean(this.properties.parameters)})
    .onCheck(checked => !checked && this.#parameters.clear());

    onChange = (action: DispatchWithoutAction) => {
        this.#configurator.onCheck(action)
        this.#parameters.onTextChanged(action)
        return this;
    }

    checked = () => this.#configurator.checked();

    configure = () => this.#parameters.text();

    draw = this.#configurator.render;
}

export const moduleParametersConfigurator = (parameters?: string) => new ModuleParametersConfigurator({parameters});
