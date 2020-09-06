import {Configurator} from "../../../framework/pattern/Configurator";
import {ProjectOpenShiftConfiguration} from "../../../model/ProjectTypes";
import {Widget} from "../../../framework/widgets/Widget";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {OPEN_SHIFT_LABEL_REGEX} from "../../../constants/Regexps";
import {horizontalGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {keyValueCollection} from "../common/PlatformCollections";
import {ManagedTextField} from "../../../framework/dsl/managed/ManagedTextField";
import {panel} from "../../../framework/dsl/managed/ManagedPanel";
import {openShiftResourceIcon} from "../icon/ResourceIcon";
import {Configurable} from "../../../framework/pattern/Configurable";
import {event} from "../../../framework/pattern/Event";
import {DispatchWithoutAction} from "react";

type Properties = {
    configuration?: ProjectOpenShiftConfiguration
}

class Configuration extends Configurable<Properties> {
    change = event()
}

export class ProjectOpenShiftConfigurator extends Widget<ProjectOpenShiftConfigurator, Properties, Configuration> implements Configurator<ProjectOpenShiftConfiguration> {
    #label = horizontalGrid({spacing: 1, alignItems: "center"})
    .pushWidget(openShiftResourceIcon())
    .pushWidget(label({
        text: "Конфигурация OpenShift",
        variant: "h6",
        color: "secondary"
    }))

    #nodeSelector = panel(keyValueCollection({
        direction: "column",
        keyRegexp: OPEN_SHIFT_LABEL_REGEX,
        valueRegexp: OPEN_SHIFT_LABEL_REGEX,
        keyPlaceholder: "platform",
        valuePlaceholder: "true",
        keyDecorator: field => field.onTextChanged(this.configuration.change.execute),
        valueDecorator: field => field.onTextChanged(this.configuration.change.execute),
        pairs: this.properties.configuration?.platformPodsNodeSelector?.map(label => ({key: label.name, value: label.value}))
    })
    .onAdd(this.configuration.change.execute)
    .onDelete(this.configuration.change.execute), {
        label: "NodeSelector для Pod-ов платформы"
    })

    #configurator = verticalGrid({spacing: 1})
    .pushWidget(this.#label)
    .pushWidget(this.#nodeSelector)

    onChange = (action: DispatchWithoutAction) => {
        this.configuration.change.handle(action);
        return this;
    }

    configure = (): ProjectOpenShiftConfiguration => ({
        platformPodsNodeSelector: this.#nodeSelector.widget().widgets().map(grid => ({
            name: (grid.getWidgetByIndex(0) as ManagedTextField).text(),
            value: (grid.getWidgetByIndex(1) as ManagedTextField).text(),
        }))
    })

    draw = this.#configurator.render;
}

export const projectOpenShiftConfigurator = (configuration?: ProjectOpenShiftConfiguration) => new ProjectOpenShiftConfigurator({configuration}, Configuration);
