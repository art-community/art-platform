import {ArtifactsResource, GitResource, OpenShiftResource} from "../model/ResourceTypes";

export const openShiftAttributes = (resource: OpenShiftResource) => [
    {
        name: "API URL",
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
    },
    {
        name: "Пароль",
        value: resource.password,
        isPassword: true
    },
];

export const gitAttributes = (resource: GitResource) => [
    {
        name: "URL",
        value: resource.url
    },
    {
        name: "Имя пользователя",
        value: resource.userName
    },
    {
        name: "Пароль",
        value: resource.password,
        isPassword: true
    },
];

export const artifactsAttributes = (resource: ArtifactsResource) => [
    {
        name: "URL",
        value: resource.url
    },
    {
        name: "Имя пользователя",
        value: resource.userName
    },
    {
        name: "Пароль",
        value: resource.password,
        isPassword: true
    },
];

