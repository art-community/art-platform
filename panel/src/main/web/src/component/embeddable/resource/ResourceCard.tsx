import * as React from "react";
import {DispatchWithoutAction} from "react";
import artifacts from "../../../images/artifacts.png"
import openShift from "../../../images/open-shift.png"
import git from "../../../images/git.png"
import proxyImage from "../../../images/proxy.png"
import EditOutlined from "@material-ui/icons/EditOutlined";
import DeleteOutlined from "@material-ui/icons/DeleteOutlined";
import {Widget} from "../../../framework/widgets/Widget";
import {warning} from "../../../framework/dsl/managed/ManagedDialog";
import {card} from "../../../framework/dsl/managed/card/ManagedCard";
import {ArtifactsResource, GitResource, OpenShiftResource, PlatformResource, ProxyResource} from "../../../model/ResourceTypes";
import {proxy} from "../../../framework/widgets/Proxy";
import {ARTIFACTS_RESOURCE, GIT_RESOURCE, OPEN_SHIFT_RESOURCE, PLATFORM_RESOURCE, PROXY_RESOURCE} from "../../../constants/ResourceConstants";
import {resourceEditingDialog} from "./ResourceEditingDialog";
import {Configurable} from "../../../framework/pattern/Configurable";
import {event} from "../../../framework/pattern/Event";
import {useResourceApi} from "../../../api/ResourceApi";
import {optional} from "../../../framework/pattern/Optional";

type ResourceCommonFields = (ArtifactsResource
    | GitResource
    | OpenShiftResource
    | PlatformResource
    | ProxyResource)
    & { type: string };

type ResourceAllFields = (ArtifactsResource
    & GitResource
    & OpenShiftResource
    & ProxyResource
    & PlatformResource)
    & { type: string };

type Properties = {
    resource: ResourceCommonFields
}

class Configuration extends Configurable<Properties> {
    delete = event();

    edit = event();
}

class ResourceCard extends Widget<ResourceCard, Properties, Configuration> {
    #api = this.hookValue(useResourceApi);

    #deleteResource = () => {
        const api = this.#api();
        switch (this.properties.resource.type) {
            case ARTIFACTS_RESOURCE:
                api.deleteArtifactsResource(this.properties.resource.id, this.configuration.delete.execute);
                return;
            case OPEN_SHIFT_RESOURCE:
                api.deleteOpenShiftResource(this.properties.resource.id, this.configuration.delete.execute);
                return;
            case GIT_RESOURCE:
                api.deleteGitResource(this.properties.resource.id, this.configuration.delete.execute);
                return;
            case PROXY_RESOURCE:
                api.deleteProxyResource(this.properties.resource.id, this.configuration.delete.execute);
                return;
        }
    }

    #avatar = () => {
        switch (this.properties.resource.type) {
            case ARTIFACTS_RESOURCE:
                return {
                    image: {
                        icon: artifacts,
                        width: 50,
                        height: 60
                    }
                };
            case OPEN_SHIFT_RESOURCE:
                return {
                    image: {
                        icon: openShift,
                        width: 60,
                        height: 50
                    }
                };
            case GIT_RESOURCE:
                return {
                    image: {
                        icon: git,
                        width: 50,
                        height: 50
                    }
                };
            case PROXY_RESOURCE:
                return {
                    image: {
                        icon: proxyImage,
                        width: 50,
                        height: 50
                    }
                };
        }
        return {}
    };

    #menu = () => ({
        actions: {
            buttons: this.properties.resource.type != PLATFORM_RESOURCE
                ? [
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
                ] :
                []
        }
    })

    #attributes = () => {
        const resource = this.properties.resource as ResourceAllFields;

        switch (resource.type) {
            case ARTIFACTS_RESOURCE:
            case PLATFORM_RESOURCE:
            case GIT_RESOURCE:
                return [
                    {
                        name: "URL",
                        link: true,
                        value: resource.url
                    },
                    {
                        name: "Имя пользователя",
                        value: resource.userName
                    }
                ]
            case PROXY_RESOURCE:
                return [
                    {
                        name: "Хост",
                        value: resource.host
                    },
                    {
                        name: "Порт",
                        value: `${resource.port}`
                    },
                    {
                        name: "Имя пользователя",
                        value: resource.userName
                    }
                ]
            case OPEN_SHIFT_RESOURCE:
                return [
                    {
                        name: "API URL",
                        link: true,
                        value: resource.apiUrl
                    },
                    {
                        name: "Домен для приложений",
                        value: resource.applicationsDomain
                    },
                    {
                        name: "URL внутреннего реестра образов",
                        value: resource.privateRegistryUrl
                    },
                    {
                        name: "Имя пользователя",
                        value: resource.userName
                    }
                ]
        }
        return []
    }

    #deletingDialog = this.add(warning({
        label: "Вы уверены, что хотите удалить ресурс?",
        cancelLabel: "Нет, оставить",
        approveLabel: "Да, удалить",
        onCancel: () => this.#deletingDialog.close(),
        onApprove: () => this.#deleteResource()
    }));

    #editingDialog = this.add(optional(() => resourceEditingDialog(this.properties.resource))
    .onDestroy(this.configuration.edit.execute));

    #card = card({label: this.properties.resource.name, panel: false})
    .configureAvatar(avatar => avatar.setAvatar(this.#avatar()))
    .configureMenu(menu => menu.setMenu(this.#menu()))
    .setAttributes(this.#attributes().filter(attribute => Boolean(attribute.value)));

    constructor(properties: Properties) {
        super(properties, Configuration)
        this.widgetName = `(${this.constructor.name}): ${this.properties.resource.id}`
    }

    onDelete = (action: DispatchWithoutAction) => {
        this.configuration.delete.handle(action)
        return this;
    }

    onEdit = (action: DispatchWithoutAction) => {
        this.configuration.edit.handle(action)
        return this;
    }

    key = () => `${this.properties.resource.id}-${this.properties.resource.type}`;

    draw = this.#card.render;
}

export const resourceCard = (resource: ResourceCommonFields) => new ResourceCard({resource});

