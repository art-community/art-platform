import * as React from "react";
import {Dispatch} from "react";
import {useTheme} from "@material-ui/core";
import {SIDE_BAR_ACTIONS} from "../../constants/SideBarActions";
import ExitToApp from "@material-ui/icons/ExitToApp";
import {useHistory} from "react-router";
import {observe} from "../../framework/pattern/Observable";
import Cookies from "js-cookie";
import {Configurable} from "../../framework/pattern/Configurable";
import {Widget} from "../../framework/widgets/Widget";
import {userAvatar} from "../embeddable/user/UserAvatar";
import {useManagementApi} from "../../api/ManagementApi";
import {label} from "../../framework/dsl/managed/ManagedLabel";
import {buttonListItem, linkListItem} from "../../framework/dsl/simple/SimpleListItem";
import {proxy} from "../../framework/widgets/Proxy";
import {group} from "../../framework/dsl/simple/SimpleGroup";
import {switcher} from "../../framework/dsl/managed/ManagedSwitch";
import {list} from "../../framework/dsl/simple/SimpleList";
import {platform} from "../entry/EntryPoint";
import {User} from "../../model/UserTypes";
import {drawer} from "../../framework/dsl/managed/ManagedDrawer";
import {verticalGrid} from "../../framework/dsl/managed/ManagedGrid";
import {hooked} from "../../framework/pattern/Hooked";
import {styled} from "../../framework/widgets/Styled";
import {conditional} from "../../framework/pattern/Conditional";
import {PlatformTheme} from "../../constants/PlatformTheme";
import {THEME_COOKIE} from "../../constants/Cookies";
import {MIN_DRAWER_WIDTH} from "../../constants/WidgetsConstants";
import {reloadContexts} from "../../context/PlatformContext";

type Properties = {
    themeName: PlatformTheme

    user: User
}

const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        root: {
            display: "flex"
        },
        userAvatar: {
            marginLeft: theme.spacing(1.2),
            marginTop: theme.spacing(2),
            marginBottom: theme.spacing(0.5)
        },
        themeSwitcher: {
            marginLeft: theme.spacing(1)
        },
        content: {
            flexGrow: 1,
            margin: theme.spacing(2)
        },
        version: {
            margin: "5px",
            display: "flex",
            justifyContent: "center"
        },
        menu: {
            flexGrow: 1
        },
        icon: {
            transform: "rotate(180deg)"
        },
        sideBarContent: {
            flexGrow: 1
        },
    }));
};

class Configuration extends Configurable<Properties> {
    themeName = this.property(this.defaultProperties.themeName);

    expanded = this.property(false);

    version = this.property();
}

class SideBar extends Widget<SideBar, Properties, Configuration> {
    #widget: Widget<any>;

    #history = this.hookValue(useHistory);

    #managementApi = this.hookValue(useManagementApi);

    #calculateThemeNameText = (mode: PlatformTheme) => this.configuration.expanded.value
        ? mode == PlatformTheme.DARK ? "Тёмная тема" : "Светлая тема"
        : "";

    #switchThemeMode = (checked: boolean) => this.configuration.themeName.value = checked
        ? PlatformTheme.DARK
        : PlatformTheme.LIGHT;

    #versionVisible = () => this.configuration.version.value && this.configuration.expanded.value;

    #themeLabel = label({
        color: "secondary",
        noWrap: true,
        text: this.#calculateThemeNameText(this.configuration.themeName.value)
    })
    .useText(text => this.configuration.themeName.consume(text.set));

    #themeSwitcher = switcher({
        label: this.#themeLabel,
        checked: this.configuration.themeName.value == PlatformTheme.DARK
    })
    .onCheck(this.#switchThemeMode);

    #menu = (actions: string[]) => SIDE_BAR_ACTIONS
    .filter(sideBarAction => actions.includes(sideBarAction.action))
    .map(action => linkListItem({
        icon: action.icon,
        text: label({color: "secondary", text: action.text}),
        path: action.path,
        itemProperties: {color: "primary"}
    })
    .onClick(reloadContexts)
    .onClick(() => this.#history().push(action.path)));

    #userAvatar = userAvatar({
        user: this.properties.user,
        fullView: this.configuration.expanded.value
    });

    #exitButton = hooked(useStyle).cache(style =>
        buttonListItem({
            icon: proxy(<ExitToApp style={style.icon} color={"secondary"}/>),
            text: label({color: "secondary", text: "Выход"}),
            itemProperties: {color: "primary"}
        })
        .onClick(platform.logOut)
    );

    #versionLabel = conditional(this.#versionVisible).persist(() =>
        hooked(useStyle).cache(style =>
            label({
                noWrap: true,
                style: style.version,
                color: "secondary",
                text: `Версия: ${this.configuration.version.value}`
            })
        )
    );

    #drawerBody = verticalGrid({wrap: "nowrap"}).breakpoints({xs: true})
    .pushWidget(
        hooked(useStyle).cache(style => group()
            .widget(styled(this.#userAvatar, style.userAvatar))
            .widget(list({style: style.menu})
                .items(this.#menu(this.properties.user.availableActions))
                .item(this.#exitButton)
            )
            .widget(styled(this.#themeSwitcher, style.themeSwitcher))
        )
    );

    #drawerContent = group()
    .widget(hooked(useStyle).cache(style => styled(this.#drawerBody, style.sideBarContent)))
    .widget(this.#versionLabel);

    #drawer = hooked(useTheme).cache(theme => drawer(this.#drawerContent, {
            expanded: this.configuration.expanded.value,
            width: Math.max(MIN_DRAWER_WIDTH, this.properties.user.name.length * theme.spacing(2))
        })
        .useExpanded(expanded => this.configuration.expanded.consume(expanded.set))
        .onExpandChanged(this.configuration.expanded.set)
    );

    #sideBar = hooked(useStyle).cache(style =>
        styled(
            group().widget(this.#drawer).widget(styled(this.#widget, style.content)),
            style.root
        )
    );

    constructor(widget: Widget<any>, properties: Properties) {
        super(properties, Configuration);
        this.#widget = widget;

        this.onLoad(() => this.#managementApi().getVersion(this.configuration.version.set));

        this.configuration.themeName
        .consume(mode => this.#themeLabel.setText(this.#calculateThemeNameText(mode)))
        .consume(mode => Cookies.set(THEME_COOKIE, mode));

        this.configuration.expanded
        .consume(() => this.#themeLabel.setText(this.#calculateThemeNameText(this.configuration.themeName.value)));

        this.configuration.expanded.consume(this.#userAvatar.setFullView);
    }

    onThemeChanged = (action: Dispatch<PlatformTheme>) => {
        this.configuration.themeName.consume(action);
        return this;
    };

    useExpanded = this.extract(configuration => configuration.expanded);

    draw = this.#sideBar.render;
}

export const sideBar = (widget: Widget<any>, properties: Properties) => new SideBar(widget, properties);
