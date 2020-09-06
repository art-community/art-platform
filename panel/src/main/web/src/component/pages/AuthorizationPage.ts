import {Dispatch} from "react";
import {useTheme,} from "@material-ui/core";
import {observe} from "../../framework/pattern/Observable";
import {ConfigurableWidget} from "../../framework/widgets/Widget";
import {Configurable} from "../../framework/pattern/Configurable";
import {text} from "../../framework/dsl/managed/ManagedTextField";
import {horizontalGrid, verticalGrid} from "../../framework/dsl/managed/ManagedGrid";
import {button} from "../../framework/dsl/managed/ManagedButton";
import {label} from "../../framework/dsl/managed/ManagedLabel";
import {useUserApi} from "../../api/UserApi";
import {ServiceExecutionException} from "../../model/ApiTypes";
import {INVALID_PASSWORD, USER_DOES_NOT_EXISTS} from "../../constants/ErrorCodes";
import {PASSWORD_REGEX, USER_NAME_REGEX} from "../../constants/Regexps";
import Cookies from "js-cookie";
import {useNotifications} from "../../framework/hooks/Hooks";
import {User} from "../../model/UserTypes";
import {container} from "../../framework/dsl/simple/SimpleContainer";
import {styled} from "../../framework/widgets/Styled";
import {hooked} from "../../framework/pattern/Hooked";
import {TOKEN_COOKIE} from "../../constants/Cookies";
import {form} from "../../framework/widgets/Form";
import {handleEnter} from "../../framework/constants/Constants";
import {platform} from "../entry/EntryPoint";
import {REGISTER_PATH} from "../../constants/Routers";

const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        container: {
            minHeight: "100vh"
        },
        buttons: {
            marginTop: theme.spacing(0.5)
        }
    }));
};

class Configuration extends Configurable {
    user = this.property<User>()
}

class Service {
    #component: AuthorizationPage;
    #api: typeof useUserApi;
    #notifications: typeof useNotifications;

    constructor(page: AuthorizationPage) {
        this.#component = page;
        this.#api = page.hookValue(useUserApi);
        this.#notifications = page.hookValue(useNotifications);
    }

    #onError = (error: ServiceExecutionException) => {
        this.#component.enable();
        switch (error.errorCode) {
            case USER_DOES_NOT_EXISTS:
                this.#notifications().error(`Пользователь ${this.#component.name} не найден`);
                return;
            case INVALID_PASSWORD:
                this.#notifications().error(`Неверный пароль`);
                return
        }
    };

    #onSuccess = (user: User) => {
        Cookies.set(TOKEN_COOKIE, user.token);
        this.#component.enable();
        this.#component.configuration.user.value = user;
    };

    authorize = () => {
        const name = this.#component.name;
        const password = this.#component.password;
        if (!name || !password) {
            return;
        }
        this.#component.disable();
        this.#api().authorize({name, password}, this.#onSuccess, this.#onError);
    };
}

class AuthorizationPage extends ConfigurableWidget<AuthorizationPage, Configuration> {
    #service = new Service(this);

    #validate = () => {
        const disabled =
            !this.#name.text() ||
            !this.#password.text() ||
            this.#name.error() ||
            this.#password.error();
        this.#authorizeButton.setDisabled(disabled)
    };

    #registerButton = button({
        label: "Я тут впервые",
        fullWidth: true,
        variant: "outlined",
        color: "secondary"
    })
    .onClick(() => platform.redirect(REGISTER_PATH));

    #authorizeButton = button({
        disabled: true,
        label: "Войти",
        fullWidth: true,
        variant: "contained",
        color: "primary",
    })
    .onClick(this.#service.authorize);

    #name = text({
        placeholder: "username",
        label: "Имя пользователя",
        required: true,
        autoFocus: true,
        autoComplete: "on",
        fullWidth: true,
        regexp: USER_NAME_REGEX,
        defaultErrorText: "Имя пользователя не должно быть пустым и содержать только символы [0-9a-zA-Z-._@]"
    })
    .onTextChanged(this.#validate);

    #password = text({
        placeholder: "password",
        label: "Пароль",
        autoComplete: "on",
        required: true,
        fullWidth: true,
        password: true,
        regexp: PASSWORD_REGEX,
        defaultErrorText: "Пароль пользователя не должен быть пустым и содержать минимум три символа"
    })
    .onTextChanged(this.#validate);

    #content = verticalGrid({spacing: 1})
    .pushWidget(label({variant: "h5", align: "center", text: "Добро пожаловать в платформу"}))
    .pushWidget(verticalGrid({spacing: 2}).pushWidget(this.#name).pushWidget(this.#password))
    .pushWidget(
        hooked(useStyle).widget(style => styled(horizontalGrid({spacing: 2})
            .breakpoints({xs: 6})
            .pushWidget(this.#authorizeButton)
            .pushWidget(this.#registerButton),
            {style: style.buttons}
        ))
    );

    #page = container(hooked(useStyle).widget(style => horizontalGrid({
        justify: "center",
        alignItems: "center",
        alignContent: "center",
        style: style.container,
        onKeyDown: handleEnter(() => this.#authorizeButton.click())
    })
    .pushWidget(form(this.#content))));

    get name() {
        return this.#name.text();
    }

    get password() {
        return this.#password.text();
    }

    authorized = (action: Dispatch<User>) => {
        this.configuration.user.consume(action);
        return this;
    };

    disable = () => {
        this.#authorizeButton.disable();
        this.#registerButton.disable();
        this.#name.disable();
        this.#password.disable();
    };

    enable = () => {
        this.#authorizeButton.enable();
        this.#registerButton.enable();
        this.#name.enable();
        this.#password.enable();
    };

    clearUser = this.configuration.user.clear;

    draw = this.#page.render;
}

export const authorizationPage = () => new AuthorizationPage(Configuration);
