import {Widget} from "../../../../framework/widgets/Widget";
import React, {DispatchWithoutAction} from "react";
import {Paper} from "@material-ui/core";
import {horizontalGrid} from "../../../../framework/dsl/managed/ManagedGrid";
import {filebeatIcon} from "../../icon/ApplicationIcon";
import {label} from "../../../../framework/dsl/managed/ManagedLabel";
import {button} from "../../../../framework/dsl/managed/ManagedButton";
import {proxy} from "../../../../framework/widgets/Proxy";
import EditOutlined from "@material-ui/icons/EditOutlined";
import DeleteOutlined from "@material-ui/icons/DeleteOutlined";
import {Configurable} from "../../../../framework/pattern/Configurable";
import {event} from "../../../../framework/pattern/Event";
import {warning} from "../../../../framework/dsl/managed/ManagedDialog";
import {moduleApplicationEditingDialog} from "./ModuleApplicationEditingDialog";
import {ModuleApplication} from "../../../../model/ModuleTypes";
import {ModuleApplicationConfigurator} from "./ModuleApplicationsCollection";
import {optional} from "../../../../framework/pattern/Optional";

type FilebeatFactory = () => ModuleApplicationConfigurator<any>;

type Properties = {
    application: ModuleApplication
    filebeatFactory: FilebeatFactory
}

class Configuration extends Configurable <Properties> {
    application = this.property(this.defaultProperties.application)
    .consume(() => this.edit.execute())

    edit = event()

    delete = event()
}

export class ModuleApplicationCard extends Widget<ModuleApplicationCard, Properties, Configuration> {
    #card = horizontalGrid({spacing: 1, alignItems: "center", wrap: "nowrap"})
    .pushWidget(filebeatIcon())
    .pushWidget(label({text: this.properties.application.applicationId.name, color: "primary"}), {xs: true})
    .pushWidget(horizontalGrid({spacing: 1, wrap: "nowrap"})
        .pushWidget(button({
            tooltip: "Изменить",
            icon: proxy(<EditOutlined color={"primary"}/>)
        }).onClick(() => this.#editingDialog.spawn()))
        .pushWidget(button({
            tooltip: "Удалить",
            icon: proxy(<DeleteOutlined color={"primary"}/>)
        }).onClick(() => this.#deletingDialog.open()))
    )

    #editingDialog = this.add(optional(() => moduleApplicationEditingDialog({
            application: this.properties.application,
            filebeatFactory: this.properties.filebeatFactory
        })
        .onEdit(this.configuration.application.set))
    )

    #deletingDialog = this.add(warning({
        label: "Вы уверены, что хотите удалить приложение?",
        cancelLabel: "Нет, оставить",
        approveLabel: "Да, удалить",
        onCancel: () => this.#deletingDialog.close(),
        onApprove: () => this.configuration.delete.execute()
    }));

    onEdit = (action: DispatchWithoutAction) => {
        this.configuration.edit.handle(action)
        return this;
    }

    onDelete = (action: DispatchWithoutAction) => {
        this.configuration.delete.handle(action)
        return this;
    }

    moduleApplication = () => this.configuration.application.value;

    applicationId = () => this.properties.application.applicationId;

    key = () => this.properties.application.applicationId.type

    draw = () => <Paper elevation={2}>{this.#card.render()}</Paper>;
}

export const moduleApplicationCard = (properties: Properties) => new ModuleApplicationCard(properties, Configuration)
