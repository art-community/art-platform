import {ManagedTextField, text} from "../../../framework/dsl/managed/ManagedTextField";
import {PASSWORD_REGEX, URL_WITHOUT_SCHEME_REGEX, USER_NAME_REGEX} from "../../../constants/Regexps";

const url = () => text({
    label: "URL репозитория",
    fullWidth: true,
    required: true,
    placeholder: "http://nexus.my.domain/maven-releases",
    regexp: URL_WITHOUT_SCHEME_REGEX,
    defaultErrorText: "URL должен соответствовать правилам наименования URL (можно без схемы)"
})

const userName = () => text({
    label: "Имя пользователя",
    fullWidth: true,
    placeholder: "userName",
    regexp: USER_NAME_REGEX,
    defaultErrorText: "Имя пользователя может содержать только символы [0-9a-zA-Z-._@]"
})

const password = () => text({
    label: "Пароль",
    fullWidth: true,
    placeholder: "password",
    regexp: PASSWORD_REGEX,
    defaultErrorText: "Пароль пользователя может содержать минимум три символа",
    password: true
})


export const artifactsResourceFields = () => new Map<string, ManagedTextField>()
.with("url", url())
.with("userName", userName())
.with("password", password());
