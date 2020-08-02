import {useTheme} from "@material-ui/core";
import {observe} from "../../../framework/pattern/Observable";
import {Widget} from "../../../framework/widgets/Widget";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {Configurable} from "../../../framework/pattern/Configurable";
import {hooked} from "../../../framework/pattern/Hooked";
import {styled} from "../../../framework/widgets/Styled";
import {group} from "../../../framework/dsl/simple/SimpleGroup";
import {horizontalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {divider} from "../../../framework/dsl/simple/SimpleDivider";
import AccountBox from "@material-ui/icons/AccountBox";
import {proxy} from "../../../framework/widgets/Proxy";
import React from "react";
import {PlatformContext, PlatformContextual} from "../../../context/PlatformContext";
import {tabs} from "../../../framework/dsl/managed/ManagedTabs";
import {when} from "../../../framework/pattern/When";
import {usersTab} from "../../embeddable/user/UsersTab";

const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        page: {
            marginLeft: theme.spacing(2),
            marginTop: theme.spacing(2),
        },
        tabs: {
            marginTop: theme.spacing(1),
        }
    }))
};

class Configuration extends Configurable<PlatformContextual> {
}

export class AdministrationPage extends Widget<AdministrationPage, PlatformContextual, Configuration> {
    #tabs = tabs({
        variant: "scrollable",
        labels: ["Пользователи"]
    })
    .onSelect(() => this.#content.notify());

    #content = group()
    .widget(this.#tabs)
    .widget(divider())
    .widget(when()
        .persist(() => this.#tabs.selected() == 0, usersTab)
    );

    #page = hooked(useStyle).widget(style => styled(group()
        .widget(horizontalGrid({spacing: 1, alignItems: "center"})
            .pushWidget(proxy(<AccountBox fontSize={"large"} color={"secondary"}/>))
            .pushWidget(label({variant: "h3", color: "primary", text: "Администрирование"}))
        )
        .widget(group()
        .widget(this.#content)), style.page)
    );

    constructor(properties: PlatformContextual) {
        super(properties, Configuration);
    }

    draw = this.#page.render;
}

export const administrationPage = (context: PlatformContext) => new AdministrationPage({context})
