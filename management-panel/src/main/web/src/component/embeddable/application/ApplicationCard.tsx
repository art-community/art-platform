import * as React from "react";
import {DispatchWithoutAction} from "react";
import filebeat from "../../../images/filebeat.png"
import EditOutlined from "@material-ui/icons/EditOutlined";
import DeleteOutlined from "@material-ui/icons/DeleteOutlined";
import {Widget} from "../../../framework/widgets/Widget";
import {warning} from "../../../framework/dsl/managed/ManagedDialog";
import {card} from "../../../framework/dsl/managed/card/ManagedCard";
import {proxy} from "../../../framework/widgets/Proxy";
import {Configurable} from "../../../framework/pattern/Configurable";
import {event} from "../../../framework/pattern/Event";
import {useApplicationApi} from "../../../api/ApplicationsApi";
import {FILEBEAT_APPLICATION} from "../../../constants/ApplicationConstants";
import {resourceIcon} from "../icon/ResourceIcon";
import {applicationEditingDialog} from "./ApplicationEditingDialog";
import {ResourceIdentifier} from "../../../model/ResourceTypes";
import {FilebeatApplication} from "../../../model/FilebeatTypes";
import {optional} from "../../../framework/pattern/Optional";

type ApplicationType = (FilebeatApplication) & { type: string };

type Properties = {
    application: ApplicationType
    resourceIds: ResourceIdentifier[]
}

class Configuration extends Configurable<Properties> {
    delete = event();

    edit = event();
}

class ApplicationCard extends Widget<ApplicationCard, Properties, Configuration> {
    #api = this.hookValue(useApplicationApi);

    #deleteApplication = () => {
        const api = this.#api();
        switch (this.properties.application.type) {
            case FILEBEAT_APPLICATION:
                api.deleteFilebeatApplication(this.properties.application.id, this.configuration.delete.execute);
                return;
        }
    }

    #avatar = () => {
        switch (this.properties.application.type) {
            case FILEBEAT_APPLICATION:
                return {
                    image: {
                        icon: filebeat,
                        width: 30,
                        height: 39
                    }
                };
        }
        throw new Error(`Unknown application type: ${this.properties.application.type}`)
    };

    #menu = () => ({
        actions: {
            buttons: [
                {
                    tooltip: "Изменить",
                    icon: proxy(<EditOutlined color={"primary"}/>),
                    onClick: () => this.#editingDialog.spawn()
                },
                {
                    tooltip: "Удалить",
                    icon: proxy(<DeleteOutlined color={"primary"}/>),
                    onClick: () => this.#deletingDialog.open()
                }
            ]
        }
    })

    #attributes = () => {
        const application = this.properties.application as FilebeatApplication & { type: string };

        switch (application.type) {
            case FILEBEAT_APPLICATION:
                return [
                    {
                        name: "URL",
                        value: application.url
                    },
                    {
                        name: "Ресурс",
                        icon: resourceIcon(application.resourceId)
                    }
                ]
        }
        throw new Error(`Unknown application type: ${application.type}`)
    }

    #deletingDialog = this.add(warning({
        label: "Вы уверены, что хотите удалить приложение?",
        cancelLabel: "Нет, оставить",
        approveLabel: "Да, удалить",
        onCancel: () => this.#deletingDialog.close(),
        onApprove: () => this.#deleteApplication()
    }));

    #editingDialog = this.add(optional(() => applicationEditingDialog(this.properties.resourceIds, this.properties.application))
        .onDestroy(this.configuration.edit.execute)
    );

    #card = card({label: this.properties.application.name, panel: false})
    .configureAvatar(avatar => avatar.setAvatar(this.#avatar()))
    .configureMenu(menu => menu.setMenu(this.#menu()))
    .setAttributes(this.#attributes());

    constructor(properties: Properties) {
        super(properties, Configuration)
        this.widgetName = `[${this.constructor.name}]: ${this.properties.application.id}`
    }

    onDelete = (action: DispatchWithoutAction) => {
        this.configuration.delete.handle(action)
        return this;
    }

    onEdit = (action: DispatchWithoutAction) => {
        this.configuration.edit.handle(action)
        return this;
    }

    key = () => `${this.properties.application.id}-${this.properties.application.type}`;

    draw = this.#card.render;
}

export const applicationCard = (resourceIds: ResourceIdentifier[], application: ApplicationType) =>
    new ApplicationCard({resourceIds, application});

