import {Configurator} from "../../../framework/pattern/Configurator";
import {ProjectNotificationsConfiguration} from "../../../model/ProjectTypes";
import {Widget} from "../../../framework/widgets/Widget";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {URL_REGEX} from "../../../constants/Regexps";
import {horizontalGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {text} from "../../../framework/dsl/managed/ManagedTextField";
import {checkBoxPanel} from "../../../framework/dsl/managed/ManagedPanel";
import {Configurable} from "../../../framework/pattern/Configurable";
import {event} from "../../../framework/pattern/Event";
import {DispatchWithoutAction} from "react";
import {resourceSelector} from "../common/PlatformSelectors";
import {ResourcesStore} from "../../../loader/ResourcesLoader";
import {PROXIES} from "../../../constants/ResourceConstants";
import {conditional} from "../../../framework/pattern/Conditional";
import {isNotEmptyArray} from "../../../framework/extensions/extensions";
import {lazy} from "../../../framework/pattern/Lazy";
import {image} from "../../../framework/dsl/simple/SimpleImage";
import notifications from "../../../images/notifications.png";

type Properties = {
    resources: ResourcesStore
    configuration?: ProjectNotificationsConfiguration
}

class Configuration extends Configurable<Properties> {
    change = event()
}

export class ProjectNotificationsConfigurator
    extends Widget<ProjectNotificationsConfigurator, Properties, Configuration>
    implements Configurator<ProjectNotificationsConfiguration> {

    #label = horizontalGrid({spacing: 1, alignItems: "center"})
    .pushWidget(image({src: notifications, width: 30, height: 30}))
    .pushWidget(label({
        text: "Конфигурация нотификаций",
        variant: "h6",
        color: "secondary"
    }));

    #url = text({
        label: "URL нотификаций",
        fullWidth: true,
        placeholder: "http://t.me/chat",
        regexp: URL_REGEX,
        defaultErrorText: "URL должен соответствовать правилам наименования URL",
        value: this.properties.configuration?.url
    })
    .onTextChanged(this.configuration.change.execute)

    #additionalMessage = text({
        label: "Дополнительное сообщение",
        fullWidth: true,
        placeholder: "Platform ❤️",
        value: this.properties.configuration?.additionalMessage
    })
    .onTextChanged(this.configuration.change.execute)

    #proxySelector = lazy(() => resourceSelector({
        ids: this.properties.resources.idsOf(PROXIES),
        label: "Прокси",
        selected: this.properties.configuration?.proxyId
    })
    .onSelect(this.configuration.change.execute))

    #proxySelectorPanel = conditional(() => isNotEmptyArray(this.properties.resources.idsOf(PROXIES)))
    .persist(() => checkBoxPanel(this.#proxySelector(), {
        label: "Использовать прокси",
        checked: Boolean(this.properties.configuration?.proxyId)
    })
    .onCheck(this.configuration.change.execute));

    #configurator = verticalGrid({spacing: 1})
    .pushWidget(this.#label)
    .pushWidget(this.#url)
    .pushWidget(this.#additionalMessage)
    .pushWidget(this.#proxySelectorPanel)

    onChange = (action: DispatchWithoutAction) => {
        this.configuration.change.handle(action);
        return this;
    }

    configure = (): ProjectNotificationsConfiguration => ({
        url: this.#url.text(),
        additionalMessage: this.#additionalMessage.text(),
        proxyId: this.#proxySelectorPanel?.get()?.checked() ? this.#proxySelector().selected() : undefined
    })

    draw = this.#configurator.render;
}

export const projectNotificationsConfigurator = (resources: ResourcesStore, configuration?: ProjectNotificationsConfiguration) =>
    new ProjectNotificationsConfigurator({configuration, resources}, Configuration);
