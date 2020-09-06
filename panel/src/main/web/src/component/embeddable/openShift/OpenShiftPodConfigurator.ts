import {horizontalGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {openShiftResourceIcon} from "../icon/ResourceIcon";
import {panel} from "../../../framework/dsl/managed/ManagedPanel";
import {keyValueCollection} from "../common/PlatformCollections";
import {OPEN_SHIFT_LABEL_REGEX} from "../../../constants/Regexps";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {Widget} from "../../../framework/widgets/Widget";
import {Configurator} from "../../../framework/pattern/Configurator";
import {ManagedTextField} from "../../../framework/dsl/managed/ManagedTextField";

export class OpenShiftPodConfigurator
    extends Widget<OpenShiftPodConfigurator>
    implements Configurator<OpenShiftPodConfiguration> {
    #label = horizontalGrid({spacing: 1, alignItems: "center"})
    .pushWidget(openShiftResourceIcon())
    .pushWidget(label({
        text: "Конфигурация OpenShift",
        variant: "h6",
        color: "secondary"
    }))

    #nodeSelectorLabelKey?: ManagedTextField
    #nodeSelectorLabelValue?: ManagedTextField

    #nodeSelectorLabels = keyValueCollection({
        direction: "column",
        keyRegexp: OPEN_SHIFT_LABEL_REGEX,
        valueRegexp: OPEN_SHIFT_LABEL_REGEX,
        keyPlaceholder: "platform",
        valuePlaceholder: "true",
        keyDecorator: field => this.#nodeSelectorLabelKey = field,
        valueDecorator: field => this.#nodeSelectorLabelValue = field
    });

    #nodeSelector = panel(this.#nodeSelectorLabels, {label: "NodeSelector"})

    #configurator = verticalGrid({spacing: 1})
    .pushWidget(this.#label)
    .pushWidget(this.#nodeSelector)

    disable = () => {
        this.#nodeSelectorLabelKey?.disable()
        this.#nodeSelectorLabelValue?.disable()
        this.#nodeSelectorLabels?.disableAddition()
        this.#nodeSelectorLabels?.disableDeletion()
    }

    enable = () => {
        this.#nodeSelectorLabelKey?.enable()
        this.#nodeSelectorLabelValue?.enable()
        this.#nodeSelectorLabels?.enableAddition()
        this.#nodeSelectorLabels?.enableDeletion()
    }

    configure = (): OpenShiftPodConfiguration => ({
        nodeSelector: this.#nodeSelector.widget().widgets().map(grid => ({
            name: (grid.getWidgetByIndex(0) as ManagedTextField).text(),
            value: (grid.getWidgetByIndex(1) as ManagedTextField).text(),
        }))
    })

    draw = this.#configurator.render;
}

export const openShiftPodConfigurator = () => new OpenShiftPodConfigurator();
