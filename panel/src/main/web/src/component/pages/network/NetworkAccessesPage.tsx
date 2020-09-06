import {useTheme} from "@material-ui/core";
import {EXECUTORS, OPEN_SHIFT_RESOURCE} from "../../../constants/ResourceConstants";
import {isNotEmptyArray} from "../../../framework/extensions/extensions";
import {observe} from "../../../framework/pattern/Observable";
import {useNetworkApi} from "../../../api/NetworkApi";
import {conditional} from "../../../framework/pattern/Conditional";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {resourceSelector} from "../../embeddable/common/PlatformSelectors";
import {text} from "../../../framework/dsl/managed/ManagedTextField";
import {group} from "../../../framework/dsl/simple/SimpleGroup";
import {divider} from "../../../framework/dsl/simple/SimpleDivider";
import {hooked} from "../../../framework/pattern/Hooked";
import {styled} from "../../../framework/widgets/Styled";
import {ResourceIdentifier} from "../../../model/ResourceTypes";
import {useNotifications} from "../../../framework/hooks/Hooks";
import {horizontalGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {PORT_REGEX, TIMEOUT_REGEX} from "../../../constants/Regexps";
import {magicLoader} from "../../embeddable/common/PlatformLoaders";
import {Widget} from "../../../framework/widgets/Widget";
import {DEFAULT_PORT, NETWORK_CHECKING_TIMEOUT} from "../../../constants/NetworkConstants";
import {openShiftPodConfigurator} from '../../embeddable/openShift/OpenShiftPodConfigurator';
import Router from "@material-ui/icons/Router";
import {proxy} from "../../../framework/widgets/Proxy";
import React from "react";
import {PlatformContext, PlatformContextual} from "../../../context/PlatformContext";


const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        page: {
            width: "max-content",
            marginLeft: theme.spacing(2),
            marginTop: theme.spacing(2),
        },
        form: {
            marginTop: theme.spacing(2),
        }
    }));
};

class NetworkAccessesPage extends Widget<NetworkAccessesPage, PlatformContextual> {
    #networkApi = this.hookValue(useNetworkApi)
    #notifications = this.hookValue(useNotifications);
    #selectedResourceId?: ResourceIdentifier;

    #validate = () => {
        const empties = !this.#hostName.text() || !this.#port.text() || !this.#timeout.text();
        const errors = this.#port.error() || this.#timeout.error();
        const disabled = empties || errors;
        this.#button.setDisabled(disabled)
    };

    #disable = () => {
        this.#hostName.disable()
        this.#port.disable()
        this.#timeout.disable()
        this.#resourceSelector.get()?.disable()
        this.#button.disable()
        this.#openShiftConfigurator.get()?.disable()
        this.notify();
    };

    #enable = () => {
        this.#hostName.enable()
        this.#port.enable()
        this.#timeout.enable()
        this.#resourceSelector.get()?.enable()
        this.#button.enable()
        this.#openShiftConfigurator.get()?.enable()
        this.notify();
    };

    #checkNetwork = () => {
        this.#disable();
        const request = {
            hostName: this.#hostName.text(),
            port: Number(this.#port.text()),
            resourceId: this.#resourceSelector.get()!.selected(),
            timeout: Number(this.#timeout.text()) * 1000,
            openShiftPodConfiguration: this.#isSelectedResourceType(OPEN_SHIFT_RESOURCE)
                ? this.#openShiftConfigurator?.get()?.configure()
                : undefined
        };
        this.#networkApi().checkNetworkAccess(request, success => {
            success ? this.#notifications().success("Доступ есть!") : this.#notifications().error("Доступа нет!");
            this.#enable();
        }, () => {
            this.#notifications().error("Доступа нет!")
            this.#enable();
        })
    };

    #isSelectedResourceType = (type: string) => () => this.#resourceSelector.get()?.selected()?.type == type;

    #loader = magicLoader(true);

    #button = button({
        variant: "contained",
        color: "primary",
        label: "Проверить",
        disabled: true
    })
    .onClick(this.#checkNetwork);

    #resourceSelector = conditional(() => isNotEmptyArray(this.properties.context.resources.ids(EXECUTORS)))
    .persist(() => resourceSelector({
            ids: this.properties.context.resources.ids(EXECUTORS),
            label: "Выберите ресурс, откуда проверять доступ"
        })
        .onSelect(id => this.#selectedResourceId = id)
    );

    #hostName = text({
        label: "Имя хоста",
        fullWidth: true,
        placeholder: "localhost",
        defaultErrorText: "Имя хоста не должно быть пустым"
    })
    .onTextChanged(this.#validate);

    #port = text({
        label: "Порт",
        fullWidth: true,
        placeholder: DEFAULT_PORT,
        regexp: PORT_REGEX,
        mask: PORT_REGEX,
        defaultErrorText: "Порт должен быть целым числом в диапазоне [0...65536]"
    })
    .useText(text => text.prevent(port => !port?.startsWith("0")))
    .onTextChanged(this.#validate);

    #timeout = text({
        label: "Таймаут в секундах",
        fullWidth: true,
        value: NETWORK_CHECKING_TIMEOUT,
        regexp: TIMEOUT_REGEX,
        mask: TIMEOUT_REGEX,
        defaultErrorText: "Таймаут должен быть целым числом"
    })
    .onTextChanged(this.#validate);

    #form = conditional(() => isNotEmptyArray(this.properties.context.resources.get.openShift))
    .persist(() =>
        verticalGrid({spacing: 1})
        .pushWidget
        (
            verticalGrid({spacing: 2})
            .pushWidget(this.#resourceSelector)
            .pushWidget(this.#hostName)
            .pushWidget(this.#port)
            .pushWidget(this.#timeout)
            .pushWidget(this.#openShiftConfigurator)
            .pushWidget(divider())
        )
        .pushWidget(conditional(this.#hostName.disabled).persist(() => this.#loader).else(this.#button)))
    .else(label({
        variant: "h5",
        color: "secondary",
        noWrap: true,
        text: "Ресурсы отсутствуют"
    }))

    #openShiftConfigurator = conditional(() => this.#isSelectedResourceType(OPEN_SHIFT_RESOURCE))
    .persist(openShiftPodConfigurator)

    #page = hooked(useStyle)
    .widget(style => styled(group()
        .widget(horizontalGrid({spacing: 1, alignItems: "center"})
            .pushWidget(proxy(<Router fontSize={"large"} color={"secondary"}/>))
            .pushWidget(label({variant: "h3", color: "primary", text: "Проверка доступов"}))
        )
        .widget(styled(this.#form, style.form)),
        style.page
    ))

    draw = this.#page.render;
}

export const networkAccessesPage = (context: PlatformContext) => new NetworkAccessesPage({context});
