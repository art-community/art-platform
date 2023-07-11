import {DispatchWithoutAction} from "react";
import {AssembledArtifact} from "../../../../model/AssemblyTypes";
import {Widget} from '../../../../framework/widgets/Widget';
import {text} from "../../../../framework/dsl/managed/ManagedTextField";
import {checkBoxPanel} from "../../../../framework/dsl/managed/ManagedPanel";
import {verticalGrid} from "../../../../framework/dsl/managed/ManagedGrid";
import {Configurator} from "../../../../framework/pattern/Configurator";
import {ModuleUrl} from "../../../../model/ModuleTypes";
import {PORT_REGEX, URL_REGEX} from "../../../../constants/Regexps";
import {DEFAULT_PORT} from "../../../../constants/NetworkConstants";

type Properties = {
    artifact: AssembledArtifact
    url?: ModuleUrl
}


export class ModuleUrlConfigurator extends Widget<ModuleUrlConfigurator, Properties> implements Configurator<ModuleUrl> {
    #url = text({
        label: "URL",
        placeholder: `http(s)://${this.properties.artifact.name}`,
        defaultErrorText: "URL должен соответствовать правилам наименования URL.",
        required: true,
        value: this.properties.url?.url,
        regexp: URL_REGEX,
        fullWidth: true
    });

    #port = text({
        label: "Порт",
        placeholder: DEFAULT_PORT,
        required: true,
        value: this.properties.url?.port || DEFAULT_PORT,
        fullWidth: true,
        regexp: PORT_REGEX,
        mask: PORT_REGEX
    })
    .useText(text => text.prevent(port => !port?.startsWith("0")));

    #configurator = checkBoxPanel(verticalGrid({spacing: 1, wrap: "nowrap"})
    .pushWidget(this.#url)
    .pushWidget(this.#port), {label: "URL", checked: Boolean(this.properties.url?.url)})
    .onCheck(checked => !checked && this.#url.clear() && this.#port.setText(DEFAULT_PORT))

    onChange = (action: DispatchWithoutAction) => {
        this.#configurator.onCheck(action)
        this.#url.onTextChanged(action)
        this.#port.onTextChanged(action)
    }

    configure = () => ({
        url: this.#url.text()?.toLowerCase(),
        port: Number(this.#port.text())
    })

    checked = () => this.#configurator.checked();

    draw = this.#configurator.render;
}

export const moduleUrlConfigurator = (properties: Properties) => new ModuleUrlConfigurator(properties);
