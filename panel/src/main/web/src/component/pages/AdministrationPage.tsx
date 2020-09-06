import {useTheme} from "@material-ui/core";
import React from "react";
import {Configurable} from "../../framework/pattern/Configurable";
import {PlatformContext, PlatformContextual} from "../../context/PlatformContext";
import {Widget} from "../../framework/widgets/Widget";
import {tabs} from "../../framework/dsl/managed/ManagedTabs";
import {group} from "../../framework/dsl/simple/SimpleGroup";
import {divider} from "../../framework/dsl/simple/SimpleDivider";
import {when} from "../../framework/pattern/When";
import {usersTab} from "../embeddable/user/UsersTab";
import {hooked} from "../../framework/pattern/Hooked";
import {horizontalGrid} from "../../framework/dsl/managed/ManagedGrid";
import {label} from "../../framework/dsl/managed/ManagedLabel";
import AccountBox from "@material-ui/icons/AccountBox";
import { styled } from '../../framework/widgets/Styled';
import {observe} from "../../framework/pattern/Observable";
import {proxy} from "../../framework/widgets/Proxy";

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
