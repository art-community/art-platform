import React, {DispatchWithoutAction} from "react";
import {Widget} from '../../../../../framework/widgets/Widget';
import {text} from "../../../../../framework/dsl/managed/ManagedTextField";
import {horizontalGrid, verticalGrid} from "../../../../../framework/dsl/managed/ManagedGrid";
import {PORT_REGEX} from "../../../../../constants/Regexps";
import {FilebeatApplication, FilebeatModuleApplication} from "../../../../../model/FilebeatTypes";
import {ResourceIdentifier} from "../../../../../model/ResourceTypes";
import {codeEditor} from "../../../../../framework/dsl/managed/ManagedCodeEditor";
import {platform} from "../../../../entry/EntryPoint";
import {PlatformTheme} from "../../../../../constants/PlatformTheme";
import {CodeEditorTheme} from "../../../../../framework/constants/Constants";
import {FILEBEAT_APPLICATION, FILEBEAT_CONFIG_FILE} from "../../../../../constants/ApplicationConstants";
import {isEmptyArray} from "../../../../../framework/extensions/extensions";
import {MODULE_APPLICATION_CONFIGURATION_EDITOR_HEIGHT} from "../../../../../constants/ModuleConstants";
import {ModuleApplicationConfigurator} from "../ModuleApplicationsCollection";
import {event} from "../../../../../framework/pattern/Event";
import {Configurable} from "../../../../../framework/pattern/Configurable";
import {ModuleConfigurator} from "../../configurator/ModuleConfigurator";
import {DEFAULT_PORT} from "../../../../../constants/NetworkConstants";

type Properties = {
    application: FilebeatApplication
    moduleApplication?: FilebeatModuleApplication
    thisModuleConfigurator: ModuleConfigurator
    resourceIds: ResourceIdentifier[]
}

class Configuration extends Configurable<Properties> {
    change = event()
}

export class ModuleFilebeatConfigurator extends Widget<ModuleFilebeatConfigurator, Properties, Configuration> implements ModuleApplicationConfigurator<FilebeatModuleApplication> {
    #port = text({
        label: "Порт",
        placeholder: DEFAULT_PORT,
        required: true,
        value: this.properties.moduleApplication?.port || DEFAULT_PORT,
        fullWidth: true,
        regexp: PORT_REGEX,
        mask: PORT_REGEX
    })
    .useText(text => text.prevent(port => !port?.startsWith("0")))
    .onTextChanged(port => {
        const configuration = this.properties.thisModuleConfigurator.configure();
        const ports = [
            ...configuration.ports || [],
            ...configuration.applications
            ?.filter(application => application.applicationId.type == FILEBEAT_APPLICATION)
            ?.map(application => (application.application as FilebeatModuleApplication).port)
            ?.filter(port => this.properties.moduleApplication ? port != this.properties.moduleApplication.port : true)
            || []
        ]
        if (isEmptyArray(ports)) {
            return
        }
        const duplicates = ports.some(current => current == Number(port));
        if (duplicates) {
            this.#port.setError({error: true, text: "Порты должны быть уникальны"})
        }
    })
    .onTextChanged(this.configuration.change.execute);

    #configuration = codeEditor({
        themeName: platform.themeName() == PlatformTheme.DARK ? CodeEditorTheme.DARK : CodeEditorTheme.LIGHT,
        fileName: FILEBEAT_CONFIG_FILE,
        value: this.properties.moduleApplication?.configuration,
        height: MODULE_APPLICATION_CONFIGURATION_EDITOR_HEIGHT
    })
    .onTextChanged(this.configuration.change.execute);

    #configurator = horizontalGrid({spacing: 1, alignItems: "flex-end"})
    .pushWidget(verticalGrid({spacing: 1, wrap: "nowrap"})
        .pushWidget(this.#port)
        .pushWidget(this.#configuration), {xs: true}
    )

    onChange = (action: DispatchWithoutAction) => {
        this.configuration.change.handle(action)
        return this
    }

    isValid = () => Boolean(this.#port.text() && !this.#port.error())

    configure = () => ({
        applicationId: {
            id: this.properties.application.id,
            type: FILEBEAT_APPLICATION,
            name: this.properties.application.name
        },
        port: Number(this.#port.text()),
        configuration: this.#configuration.text(),
        url: this.properties.application.url,
        resourceId: this.properties.application.resourceId
    })

    draw = this.#configurator.render;
}

export const moduleFilebeatConfigurator = (properties: Properties) => new ModuleFilebeatConfigurator(properties, Configuration);
