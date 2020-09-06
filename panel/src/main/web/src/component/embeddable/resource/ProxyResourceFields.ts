import {ManagedTextField, text} from "../../../framework/dsl/managed/ManagedTextField";
import {HOST_NAME_AND_IP_REGEX, PASSWORD_REGEX, PORT_REGEX, URL_WITHOUT_SCHEME_REGEX, USER_NAME_REGEX} from "../../../constants/Regexps";
import {DEFAULT_PORT} from "../../../constants/NetworkConstants";

const hostOrIp = () => text({
    label: "Хост или IP адрес",
    fullWidth: true,
    placeholder: "localhost",
    regexp: HOST_NAME_AND_IP_REGEX,
    required: true
})

const port = () => text({
    label: "Порт",
    fullWidth: true,
    placeholder: DEFAULT_PORT,
    regexp: PORT_REGEX,
    mask: PORT_REGEX
})
.valueMapper(Number)
.useText(text => text.prevent(port => !port?.startsWith("0")))

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

export const proxyResourceFields = () => new Map<string, ManagedTextField>()
.with("host", hostOrIp())
.with("port", port())
.with("userName", userName())
.with("password", password());
