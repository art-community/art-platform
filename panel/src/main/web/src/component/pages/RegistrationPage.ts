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
import {EMAIL_REGEX, FULL_NAME_REGEX, PASSWORD_REGEX, USER_NAME_REGEX} from "../../constants/Regexps";
import Cookies from "js-cookie";
import {useNotifications} from "../../framework/hooks/Hooks";
import {User} from "../../model/UserTypes";
import {container} from "../../framework/dsl/simple/SimpleContainer";
import {hooked} from "../../framework/pattern/Hooked";
import {styled} from "../../framework/widgets/Styled";
import {TOKEN_COOKIE} from "../../constants/Cookies";
import {form} from "../../framework/widgets/Form";
import {handleEnter} from "../../framework/constants/Constants";
import {platform} from "../entry/EntryPoint";
import {AUTHORIZE_PATH} from "../../constants/Routers";

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
    #component: RegistrationPage;
    #api: typeof useUserApi;
    #notifications: typeof useNotifications;

    constructor(component: RegistrationPage) {
        this.#component = component;
        this.#api = component.hookValue(useUserApi);
        this.#notifications = component.hookValue(useNotifications);
    }

    #onError = () => {
        this.#component.enable();
    };

    #onSuccess = (user: User) => {
        Cookies.set(TOKEN_COOKIE, user.token);
        this.#component.enable();
        this.#component.configuration.user.value = user;
    };

    register = () => {
        const name = this.#component.name;
        const password = this.#component.password;
        const email = this.#component.email;
        const fullName = this.#component.fullName;
        if (!name || !password || !email || !fullName) {
            return;
        }
        this.#component.disable();
        const request = {name, password, fullName, email};
        this.#api().registerUser(request, this.#onSuccess, this.#onError);
    };
}

class RegistrationPage extends ConfigurableWidget<RegistrationPage, Configuration> {
    #userNames: string[] = [];

    #userEmails: string[] = [];

    #api = this.hookValue(useUserApi);

    #service = new Service(this);

    #validate = () => {
        this.#validateDuplicates();

        const empties =
            !this.#name.text() ||
            !this.#password.text() ||
            !this.#fullName.text() ||
            !this.#email.text();

        const errors =
            this.#name.error() ||
            this.#email.error() ||
            this.#password.error() ||
            this.#password.error();

        const disabled = empties || errors;
        this.#continueButton.setDisabled(disabled)
    };

    #validateDuplicates = () => {
        if (this.#userNames.includes(this.#name.text())) {
            this.#name.setError({error: true, text: "Имя пользователя занято"})
        }
        if (this.#userEmails.includes(this.#email.text())) {
            this.#email.setError({error: true, text: "Пользователь с такой почтой уже зарегистрирован"})
        }
    };

    #continueButton = button({
        label: "Продолжить",
        fullWidth: true,
        variant: "contained",
        disabled: true,
        color: "primary",
    })
    .onClick(this.#service.register);

    #returnButton = button({
        label: "Вернуться",
        fullWidth: true,
        variant: "outlined",
        color: "secondary"
    })
    .onClick(() => platform.redirect(AUTHORIZE_PATH));

    #fullName = text({
        placeholder: "Админов Админ Админович",
        autoFocus: true,
        label: "ФИО",
        required: true,
        fullWidth: true,
        regexp: FULL_NAME_REGEX,
        defaultErrorText: "ФИО пользователя не должно быть пустым и содержать минимум три символа"
    })
    .onTextChanged(this.#validate);

    #email = text({
        placeholder: "admin@admin.ru",
        label: "Почта",
        required: true,
        fullWidth: true,
        regexp: EMAIL_REGEX,
        defaultErrorText: "Почта должна быть должна соответствовать правилам e-mail"
    })
    .onTextChanged(this.#validate);

    #name = text({
        placeholder: "username",
        label: "Имя пользователя",
        autoComplete: "on",
        required: true,
        fullWidth: true,
        regexp: USER_NAME_REGEX,
        defaultErrorText: "Имя пользователя не должно быть пустым и содержать только символы [0-9a-zA-Z-._@]"
    })
    .onTextChanged(this.#validate);

    #password = text({
        placeholder: "password",
        label: "Пароль",
        required: true,
        fullWidth: true,
        autoComplete: "on",
        password: true,
        regexp: PASSWORD_REGEX,
        defaultErrorText: "Пароль пользователя не должен быть пустым и содержать минимум три символа"
    })
    .onTextChanged(this.#validate);

    #content = verticalGrid({spacing: 1})
    .pushWidget(label({variant: "h5", align: "center", text: "Регистрация"}))

    .pushWidget(verticalGrid({spacing: 2})
    .pushWidget(this.#fullName)
    .pushWidget(this.#email)
    .pushWidget(this.#name)
    .pushWidget(this.#password))
    .pushWidget(hooked(useStyle).widget(style => styled(horizontalGrid({spacing: 2})
        .breakpoints({xs: 6})
        .pushWidget(this.#continueButton)
        .pushWidget(this.#returnButton),
        style.buttons)
    ));

    #page = container(hooked(useStyle).widget(style => horizontalGrid({
        justify: "center",
        alignItems: "center",
        alignContent: "center",
        style: style.container,
        onKeyDown: handleEnter(() => this.#continueButton.click())
    })
    .pushWidget(form(this.#content))));

    constructor() {
        super(Configuration);
        this.onLoad(() => {
            this.#api().getUserNames(names => {
                this.#userNames = [...names];
                this.#validateDuplicates();
            });
            this.#api().getUserEmails(emails => {
                this.#userEmails = [...emails];
                this.#validateDuplicates();
            });
        });
    }

    get name() {
        return this.#name.text();
    }

    get password() {
        return this.#password.text();
    }

    get fullName() {
        return this.#fullName.text();
    }

    get email() {
        return this.#email.text();
    }

    registered = (action: Dispatch<User>) => {
        this.configuration.user.consume(action);
        return this;
    };

    disable = () => {
        this.#continueButton.disable();
        this.#continueButton.disable();
        this.#name.disable();
        this.#fullName.disable();
        this.#email.disable();
        this.#password.disable();
    };

    enable = () => {
        this.#continueButton.enable();
        this.#continueButton.enable();
        this.#name.enable();
        this.#fullName.disable();
        this.#email.disable();
        this.#password.enable();
    };

    clearUser = this.configuration.user.clear;

    draw = this.#page.render;
}

export const registrationPage = () => new RegistrationPage();
