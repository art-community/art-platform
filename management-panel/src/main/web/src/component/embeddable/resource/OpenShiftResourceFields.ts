import {ManagedTextField, text} from "../../../framework/dsl/managed/ManagedTextField";
import {HOST_NAME_REGEX, PASSWORD_REGEX, URL_REGEX, URL_WITHOUT_SCHEME_REGEX, USER_NAME_REGEX} from "../../../constants/Regexps";

const apiUrl = () => text({
    label: "API URL",
    fullWidth: true,
    required: true,
    placeholder: "https://api.my.domain:6443/",
    regexp: URL_REGEX,
    defaultErrorText: "URL должен соответствовать правилам наименования URL"
})

const registryUrl = () => text({
    label: "URL реестра образов",
    fullWidth: true,
    required: true,
    placeholder: "image-registry.openshift-image-registry.svc:5000",
    regexp: URL_WITHOUT_SCHEME_REGEX,
    defaultErrorText: "URL должен соответствовать правилам наименования URL (можно без схемы)"
})

const applicationsDomain = () => text({
    label: "Домен для приложений",
    fullWidth: true,
    required: true,
    placeholder: "apps.my.domain",
    regexp: HOST_NAME_REGEX,
    defaultErrorText: "Имя домена должно соответствовать правилам наименования доменных имён"
})

const userName = () => text({
    label: "Имя пользователя",
    fullWidth: true,
    required: true,
    placeholder: "userName",
    regexp: USER_NAME_REGEX,
    defaultErrorText: "Имя пользователя не должно быть пустым и содержать только символы [0-9a-zA-Z-._@]"
})

const password = () => text({
    label: "Пароль",
    fullWidth: true,
    required: true,
    placeholder: "password",
    regexp: PASSWORD_REGEX,
    defaultErrorText: "Пароль пользователя не должен быть пустым и должен содержать минимум три символа",
    password: true
})

export const openShiftResourceFields = () => new Map<string, ManagedTextField>()
.with("apiUrl", apiUrl())
.with("privateRegistryUrl", registryUrl())
.with("applicationsDomain", applicationsDomain())
.with("userName", userName())
.with("password", password());
