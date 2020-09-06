import * as React from "react";
import {useTheme} from "@material-ui/core";
import {observe} from "../../framework/pattern/Observable";
import {label} from "../../framework/dsl/managed/ManagedLabel";
import {horizontalGrid, verticalGrid} from "../../framework/dsl/managed/ManagedGrid";
import {Widget} from "../../framework/widgets/Widget";
import {button} from "../../framework/dsl/managed/ManagedButton";
import {hooked} from "../../framework/pattern/Hooked";
import {styled} from "../../framework/widgets/Styled";
import {ARTIFACTS_RESOURCE, GIT_RESOURCE, OPEN_SHIFT_RESOURCE, PLATFORM_RESOURCE, PROXY_RESOURCE} from "../../constants/ResourceConstants";
import {resourceCard} from "../embeddable/resource/ResourceCard";
import {divider} from "../../framework/dsl/simple/SimpleDivider";
import AddOutlined from "@material-ui/icons/AddOutlined";
import {group} from "../../framework/dsl/simple/SimpleGroup";
import {proxy} from "../../framework/widgets/Proxy";
import {resourceAdditionDialog} from "../embeddable/resource/ResourceAdditionDialog";
import Computer from "@material-ui/icons/Computer";
import {PlatformContext, PlatformContextual} from "../../context/PlatformContext";
import {optional} from "../../framework/pattern/Optional";

const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        page: {
            marginLeft: theme.spacing(2),
            marginTop: theme.spacing(2),
        },
        cards: {
            marginTop: theme.spacing(2),
        }
    }))
};

class ResourcesPage extends Widget<ResourcesPage, PlatformContextual> {
    #resourceLoader = this.properties.context.resources;

    #reload = () => this.#resourceLoader.load(this.notify);

    #addButton = () => button({icon: proxy(<AddOutlined/>), tooltip: "Добавить", color: "primary"});

    #cards = () => {
        const {artifacts, git, proxy, openShift, platform} = this.#resourceLoader.get;
        return verticalGrid({spacing: 1})
        .pushWidget(verticalGrid()
            .pushWidget(label({color: "secondary", variant: "h6", text: "Артефакты"}))
            .pushWidget(divider(1, 1))
        )
        .pushWidget(horizontalGrid({spacing: 1, alignItems: "center"})
            .pushWidgets(artifacts.map(resource => resourceCard({...resource, type: ARTIFACTS_RESOURCE})
            .onEdit(this.#reload)
            .onDelete(this.#reload)))
            .pushWidget(this.#addButton().onClick(() => this.#artifactsAdditionDialog.spawn()))
        )
        .pushWidget(verticalGrid()
            .pushWidget(label({color: "secondary", variant: "h6", text: "OpenShift платформы"}))
            .pushWidget(divider(1, 1))
        )
        .pushWidget(horizontalGrid({spacing: 1, alignItems: "center"})
            .pushWidgets(openShift.map(resource => resourceCard({...resource, type: OPEN_SHIFT_RESOURCE})
            .onEdit(this.#reload)
            .onDelete(this.#reload)))
            .pushWidget(this.#addButton().onClick(() => this.#openShiftAdditionDialog.spawn()))
        )
        .pushWidget(verticalGrid()
            .pushWidget(label({color: "secondary", variant: "h6", text: "Репозитории"}))
            .pushWidget(divider(1, 1))
        )
        .pushWidget(horizontalGrid({spacing: 1, alignItems: "center"})
            .pushWidgets(git.map(resource => resourceCard({...resource, type: GIT_RESOURCE})
            .onEdit(this.#reload)
            .onDelete(this.#reload)))
            .pushWidget(this.#addButton().onClick(() => this.#gitAdditionDialog.spawn()))
        )
        .pushWidget(verticalGrid()
            .pushWidget(label({color: "secondary", variant: "h6", text: "Прокси"}))
            .pushWidget(divider(1, 1))
        )
        .pushWidget(horizontalGrid({spacing: 1, alignItems: "center"})
            .pushWidgets(proxy.map(resource => resourceCard({...resource, type: PROXY_RESOURCE})
            .onEdit(this.#reload)
            .onDelete(this.#reload)))
            .pushWidget(this.#addButton().onClick(() => this.#proxyAdditionDialog.spawn()))
        )
        .pushWidget(verticalGrid()
            .pushWidget(label({color: "secondary", variant: "h6", text: "Внешние платформы"}))
            .pushWidget(divider(1, 1))
        )
        .pushWidgets(platform.map(resource => resourceCard({...resource, type: PLATFORM_RESOURCE})));
    };

    #openShiftAdditionDialog = this.add(optional(() => resourceAdditionDialog(OPEN_SHIFT_RESOURCE)).onDestroy(this.#reload));

    #gitAdditionDialog = this.add(optional(() => resourceAdditionDialog(GIT_RESOURCE)).onDestroy(this.#reload));

    #artifactsAdditionDialog = this.add(optional(() => resourceAdditionDialog(ARTIFACTS_RESOURCE)).onDestroy(this.#reload));

    #proxyAdditionDialog = this.add(optional(() => resourceAdditionDialog(PROXY_RESOURCE)).onDestroy(this.#reload));

    #page = hooked(useStyle).widget(style =>
        styled(
            group()
            .widget(horizontalGrid({spacing: 1, alignItems: "center"})
                .pushWidget(proxy(<Computer fontSize={"large"} color={"secondary"}/>))
                .pushWidget(label({color: "primary", variant: "h3", text: "Ресурсы"}))
            )
            .widget(styled(this.#cards(), style.cards)), style.page)
    );

    draw = this.#page.render;
}

export const resourcesPage = (context: PlatformContext) => new ResourcesPage({context});
