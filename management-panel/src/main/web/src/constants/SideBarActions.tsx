import {ADMINISTRATION_PATH, APPLICATIONS_PATH, ASSEMBLIES_PATH, CONFIGURATIONS_PATH, MODULES_PATH, NETWORK_ACCESSES_PATH, PROJECTS_PATH, RESOURCES_PATH} from "./Routers";
import FeaturedPlayList from "@material-ui/icons/FeaturedPlayList";
import Computer from "@material-ui/icons/Computer";
import LoopOutlined from "@material-ui/icons/LoopOutlined";
import ViewModule from "@material-ui/icons/ViewModule";
import Router from "@material-ui/icons/Router";
import Category from "@material-ui/icons/Category";
import AccountBox from "@material-ui/icons/AccountBox";
import TuneOutlined from "@material-ui/icons/TuneOutlined";
import * as React from "react";
import {
    ADMINISTRATION,
    APPLICATIONS_MANAGEMENT,
    ASSEMBLIES_MANAGEMENT,
    CONFIGURATIONS_MANAGEMENT,
    MODULES_MANAGEMENT,
    NETWORK_ACCESSES_CHECKING,
    PROJECTS_MANAGEMENT,
    RESOURCES_MANAGEMENT
} from "./UserActions";
import {proxy} from "../framework/widgets/Proxy";

export const SIDE_BAR_ACTIONS =
    [
        {
            action: PROJECTS_MANAGEMENT.action,
            icon: proxy(<FeaturedPlayList color={"secondary"}/>),
            text: "Проекты",
            path: PROJECTS_PATH
        },
        {
            action: RESOURCES_MANAGEMENT.action,
            icon: proxy(<Computer color={"secondary"}/>),
            text: "Ресурсы",
            path: RESOURCES_PATH
        },
        {
            action: ASSEMBLIES_MANAGEMENT.action,
            icon: proxy(<LoopOutlined color={"secondary"}/>),
            text: "Сборки",
            path: ASSEMBLIES_PATH
        },
        {
            action: MODULES_MANAGEMENT.action,
            icon: proxy(<ViewModule color={"secondary"}/>),
            text: "Модули",
            path: MODULES_PATH
        },
        {
            action: CONFIGURATIONS_MANAGEMENT.action,
            icon: proxy(<TuneOutlined color={"secondary"}/>),
            text: "Конфигурации",
            path: CONFIGURATIONS_PATH
        },
        {
            action: NETWORK_ACCESSES_CHECKING.action,
            icon: proxy(<Router color={"secondary"}/>),
            text: "Доступы",
            path: NETWORK_ACCESSES_PATH
        },
        {
            action: APPLICATIONS_MANAGEMENT.action,
            icon: proxy(<Category color={"secondary"}/>),
            text: "Приложения",
            path: APPLICATIONS_PATH
        },
        {
            action: ADMINISTRATION.action,
            icon: proxy(<AccountBox color={"secondary"}/>),
            text: "Администрирование",
            path: ADMINISTRATION_PATH
        }
    ];

export const actionOfPath = (path: string) => SIDE_BAR_ACTIONS.find(action => action.path == path)
