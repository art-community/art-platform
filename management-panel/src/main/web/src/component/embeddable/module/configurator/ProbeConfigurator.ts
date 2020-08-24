import {Widget} from "../../../../framework/widgets/Widget";
import {checkBoxPanel} from "../../../../framework/dsl/managed/ManagedPanel";
import {text} from "../../../../framework/dsl/managed/ManagedTextField";
import {Configurator} from "../../../../framework/pattern/Configurator";
import {DispatchWithoutAction} from "react";
import {ProbeConfiguration} from "../../../../model/ModuleTypes";
import {checkbox} from "../../../../framework/dsl/managed/ManagedCheckbox";
import {verticalGrid} from "../../../../framework/dsl/managed/ManagedGrid";
import {PATH_REGEX, PORT_REGEX} from "../../../../constants/Regexps";

type Properties = {
    probeConfiguration?: ProbeConfiguration
}

export class ProbeConfigurator extends Widget<ProbeConfigurator, Properties> implements Configurator<ProbeConfiguration> {
    #path = text({
        label: "Путь",
        placeholder: "/status",
        required: true,
        value: this.properties?.probeConfiguration?.path,
        regexp: PATH_REGEX,
        fullWidth: true
    });

    #livenessProbe = checkbox({
        label: "Проверка доступности модуля"
    }).setChecked(this.properties?.probeConfiguration?.livenessProbe || false);

    #readinessProbe = checkbox({
        label: "Проверка готовности модуля"
    }).setChecked(this.properties?.probeConfiguration?.readinessProbe || false);

    #configurator = checkBoxPanel(verticalGrid({spacing: 1, wrap: "nowrap"})
            .pushWidget(this.#path)
            .pushWidget(this.#livenessProbe)
            .pushWidget(this.#readinessProbe),
        {label: "Проверки доступности", checked: Boolean(this.properties.probeConfiguration?.path)})
        .onCheck(checked => !checked && this.#path.clear() && this.#livenessProbe.uncheck() && this.#readinessProbe.uncheck());

    onChange = (action: DispatchWithoutAction) => {
        this.#configurator.onCheck(action)
        this.#path.onTextChanged(action)
        return this;
    }

    checked = () => this.#configurator.checked();

    configure = () => ({
        path: this.#path.text(),
        livenessProbe: this.#livenessProbe.checked(),
        readinessProbe: this.#readinessProbe.checked()
    });

    draw = this.#configurator.render;
}

export const probeConfigurator = (probeConfiguration: { probeConfiguration?: ProbeConfiguration }) => new ProbeConfigurator(probeConfiguration);
