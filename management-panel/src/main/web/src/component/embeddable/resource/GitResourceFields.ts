import {ManagedTextField, text} from "../../../framework/dsl/managed/ManagedTextField";
import {GIT_URL_REGEX, PASSWORD_REGEX, USER_NAME_REGEX} from "../../../constants/Regexps";

const url = () => text({
    label: "URL репозитория",
    fullWidth: true,
    required: true,
    placeholder: "https://github.com/art-community/art-java.git",
    regexp: GIT_URL_REGEX,
    defaultErrorText: "URL должен соответствовать правилам наименования Git URL"
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


export const gitResourceFields = () => new Map<string, ManagedTextField>()
.with("url", url())
.with("userName", userName())
.with("password", password());
