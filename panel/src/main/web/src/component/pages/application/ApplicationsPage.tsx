import * as React from "react";
import {useTheme} from "@material-ui/core";
import {observe} from "../../../framework/pattern/Observable";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {horizontalGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {Widget} from "../../../framework/widgets/Widget";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {hooked} from "../../../framework/pattern/Hooked";
import {styled} from "../../../framework/widgets/Styled";
import {conditional} from "../../../framework/pattern/Conditional";
import {REGISTRIES} from "../../../constants/ResourceConstants";
import {divider} from "../../../framework/dsl/simple/SimpleDivider";
import AddOutlined from "@material-ui/icons/AddOutlined";
import {group} from "../../../framework/dsl/simple/SimpleGroup";
import {proxy} from "../../../framework/widgets/Proxy";
import {FILEBEAT_APPLICATION} from "../../../constants/ApplicationConstants";
import {applicationCard} from "../../embeddable/application/ApplicationCard";
import {applicationAdditionDialog} from "../../embeddable/application/ApplicationAdditionDialog";
import {isNotEmptyArray} from "../../../framework/extensions/extensions";
import Category from "@material-ui/icons/Category";
import {PlatformContext, PlatformContextual} from "../../../context/PlatformContext";
import {optional} from '../../../framework/pattern/Optional';

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

class ApplicationsPage extends Widget<ApplicationsPage, PlatformContextual> {
    #applicationsLoader = this.properties.context.applications;

    #reload = () => this.#applicationsLoader.load(this.notify);

    #addButton = () => button({icon: proxy(<AddOutlined/>), tooltip: "Добавить", color: "primary"});

    #noResourcesLabel = label({
        variant: "h5",
        color: "secondary",
        text: "Ресурсы отсутствуют"
    })

    #cards = () => {
        const {filebeat} = this.#applicationsLoader.get;
        return conditional(() => isNotEmptyArray(this.properties.context.resources.ids(REGISTRIES)))
        .persist(() => verticalGrid({spacing: 1})
            .pushWidget(verticalGrid()
                .pushWidget(label({color: "secondary", variant: "h6", text: "Filebeat"}))
                .pushWidget(divider(1, 1))
            )
            .pushWidget(horizontalGrid({spacing: 1, alignItems: "center"})
                .pushWidgets(filebeat.map(application => applicationCard(this.properties.context.resources.ids(REGISTRIES), {
                    ...application,
                    type: FILEBEAT_APPLICATION
                })
                .onEdit(this.#reload)
                .onDelete(this.#reload)))
                .pushWidget(this.#addButton().onClick(() => this.#filebeatAdditionDialog.spawn()))
            )
        )
        .else(this.#noResourcesLabel)
    };

    #filebeatAdditionDialog = this.add(
        optional(() => applicationAdditionDialog(this.properties.context.resources.ids(REGISTRIES), FILEBEAT_APPLICATION)).onDestroy(this.#reload)
    );

    #page = hooked(useStyle).widget(style => styled(group()
        .widget(horizontalGrid({spacing: 1, alignItems: "center"})
            .pushWidget(proxy(<Category fontSize={"large"} color={"secondary"}/>))
            .pushWidget(label({color: "primary", variant: "h3", text: "Приложения"}))
        )
        .widget(styled(this.#cards(), style.cards)), style.page)
    );

    draw = this.#page.render;
}

export const applicationsPage = (context: PlatformContext) => new ApplicationsPage({context});
