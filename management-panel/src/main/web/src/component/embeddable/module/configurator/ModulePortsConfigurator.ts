import {DispatchWithoutAction} from "react";
import {Widget} from "../../../../framework/widgets/Widget";
import {portsCollection} from "../../common/PlatformCollections";
import {checkBoxPanel} from "../../../../framework/dsl/managed/ManagedPanel";
import {verticalGrid} from "../../../../framework/dsl/managed/ManagedGrid";
import {Configurator} from "../../../../framework/pattern/Configurator";
import {isNotEmptyArray} from "../../../../framework/extensions/extensions";
import {Configurable} from "../../../../framework/pattern/Configurable";
import {event} from "../../../../framework/pattern/Event";

type Properties = {
    ports?: number[]
}

class Configuration extends Configurable<Properties> {
    change = event();
}

export class ModulePortsConfigurator extends Widget<ModulePortsConfigurator, Properties, Configuration> implements Configurator<number[]> {
    #ports = portsCollection({ports: this.properties.ports, decorator: port => port.onTextChanged(this.configuration.change.execute)})
    .onAdd(this.configuration.change.execute)
    .onDelete(this.configuration.change.execute)

    #configurator = checkBoxPanel(verticalGrid({wrap: "nowrap"}).pushWidget(this.#ports), {
        label: "Порты",
        checked: isNotEmptyArray(this.properties.ports)
    })
    .onCheck(checked => !checked && this.#ports.clear())
    .onCheck(this.configuration.change.execute)

    onChange = (action: DispatchWithoutAction) => {
        this.configuration.change.handle(action)
        return this;
    }

    configure = () => this.#ports.widgets().map(port => Number(port.text()))

    checked = () => this.#configurator.checked();

    draw = this.#configurator.render;
}

export const modulePortsConfigurator = (properties: Properties) => new ModulePortsConfigurator(properties, Configuration);
