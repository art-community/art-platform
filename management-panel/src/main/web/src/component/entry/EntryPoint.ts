import {
    ADMINISTRATION_PATH,
    APPLICATIONS_PATH,
    ASSEMBLIES_PATH,
    AUTHORIZE_PATH,
    CONFIGURATIONS_PATH,
    MODULES_PATH,
    NETWORK_ACCESSES_PATH,
    PLATFORM_PATH,
    PROJECTS_PATH,
    REGISTER_PATH,
    RESOURCES_PATH,
    ROUTING_DEFAULT_PATH,
    SLASH
} from "../../constants/Routers";
import {Configurable} from "../../framework/pattern/Configurable";
import {authenticate} from "../../api/UserApi";
import {sideBar} from "../sidebar/SideBar";
import Cookies from "js-cookie";
import {bigLoader} from "../../framework/dsl/simple/SimpleLoader";
import {registrationPage} from "../pages/registration/RegistrationPage";
import {authorizationPage} from '../pages/authorization/AuthorizationPage';
import {ConfigurableWidget, Widget} from "../../framework/widgets/Widget";
import {streamsActivator} from "../stream/StreamsActivator";
import {User} from "../../model/UserTypes";
import {browserRouter, redirect, routingSwitch} from "../../framework/dsl/simple/SimpleRouter";
import {provideTheme} from "../../framework/dsl/managed/ManagedThemeProvider";
import {group} from "../../framework/dsl/simple/SimpleGroup";
import {conditional} from "../../framework/pattern/Conditional";
import {projectsPage} from "../pages/project/ProjectsPage";
import {resourcesPage} from "../pages/resource/ResourcesPage";
import {provideSnackbar} from "../../framework/dsl/simple/SimpleSnackbarProvider";
import {assembliesPage} from "../pages/assemblies/AssembliesPage";
import {THEME_COOKIE, TOKEN_COOKIE} from "../../constants/Cookies";
import {PlatformTheme, THEMES} from "../../constants/PlatformTheme";
import {MAX_SNAKES} from "../../constants/WidgetsConstants";
import {Dispatch} from "react";
import {Theme} from "@material-ui/core";
import {modulesPage} from "../pages/module/ModulesPage";
import {cssBaseLine} from "../../framework/widgets/CssBaseLine";
import {applicationsPage} from '../pages/application/ApplicationsPage';
import {platformContext} from "../../context/PlatformContext";
import {actionOfPath} from "../../constants/SideBarActions";
import {administrationPage} from "../pages/administration/AssembliesPage";
import {networkAccessesPage} from "../pages/network/NetworkAccessesPage";
import {configurationsPage} from "../pages/configuration/ConfigurationsPage";
import {
    ADMINISTRATION_CONTEXT,
    APPLICATIONS_CONTEXT,
    ASSEMBLIES_CONTEXT,
    MODULES_CONTEXT, NETWORK_ACCESSES_CONTEXT,
    PREPARED_CONFIGURATIONS_CONTEXT,
    PROJECTS_CONTEXT,
    RESOURCES_CONTEXT
} from "../../constants/ContextConstants";

class Configuration extends Configurable {
    user = this.property<User>();

    initialized = this.property(false);

    themeName = this.property(Cookies.get(THEME_COOKIE) as PlatformTheme || PlatformTheme.LIGHT);
}

class EntryPoint extends ConfigurableWidget<EntryPoint, Configuration> {
    #referer = ROUTING_DEFAULT_PATH;

    #loader = bigLoader();

    #authorized = () => Cookies.get(TOKEN_COOKIE) && this.configuration.user.value;

    #initialized = () => this.configuration.initialized.value;

    #privileged = (path: string) => {
        const action = actionOfPath(path);
        return this.#authorized() && action && this.configuration.user.value!.availableActions!.includes(action.action);
    }

    #routeToPrivatePath = (path: string, page: Widget<any>) => conditional(() => this.#privileged(path))
    .persist(() => page.onRender(() => this.#referer = this.location().pathname))
    .else(redirect(AUTHORIZE_PATH).onRender(() => this.#referer = this.location().pathname));

    #routeToPublicPath = (path: string, page: Widget<any>) => conditional(this.#authorized)
    .persist(() => redirect(this.#privileged(this.#referer) ? this.#referer : ROUTING_DEFAULT_PATH))
    .else(page);

    #authenticate = () => {
        const {user, initialized} = this.configuration;
        const token = Cookies.get(TOKEN_COOKIE) as string;
        if (!token) {
            this.lock(() => {
                user.clear();
                initialized.value = true;
            });
            return;
        }
        authenticate(token, loadedUser => this.lock(() => {
                initialized.value = true;
                user.value = loadedUser;
            }),
            () => this.lock(() => {
                initialized.value = true;
                user.clear();
            })
        );
    };

    #pagesSwitch = routingSwitch()
    .addRoute(PROJECTS_PATH, () => this.#routeToPrivatePath(PROJECTS_PATH, platformContext(PROJECTS_CONTEXT, projectsPage)
        .includeResources()
        .includeProjects())
    )
    .addRoute(RESOURCES_PATH, () => this.#routeToPrivatePath(RESOURCES_PATH, platformContext(RESOURCES_CONTEXT, resourcesPage)
        .includeResources())
    )
    .addRoute(ASSEMBLIES_PATH, () => this.#routeToPrivatePath(ASSEMBLIES_PATH, platformContext(ASSEMBLIES_CONTEXT, assembliesPage)
        .includeResources()
        .includeProjects()
        .includeApplications()
        .includePreparedConfigurations()
        .includeAssemblies())
    )
    .addRoute(MODULES_PATH, () => this.#routeToPrivatePath(MODULES_PATH, platformContext(MODULES_CONTEXT, modulesPage)
        .includeAssemblies()
        .includeProjects()
        .includeModules()
        .includeResources()
        .includePreparedConfigurations()
        .includeApplications())
    )
    .addRoute(CONFIGURATIONS_PATH, () => this.#routeToPrivatePath(CONFIGURATIONS_PATH, platformContext(PREPARED_CONFIGURATIONS_CONTEXT, configurationsPage)
        .includePreparedConfigurations()
        .includeModules()
        .includeProjects())
    )
    .addRoute(NETWORK_ACCESSES_PATH, () => this.#routeToPrivatePath(NETWORK_ACCESSES_PATH, platformContext(NETWORK_ACCESSES_CONTEXT, networkAccessesPage)
        .includeResources())
    )
    .addRoute(APPLICATIONS_PATH, () => this.#routeToPrivatePath(APPLICATIONS_PATH, platformContext(APPLICATIONS_CONTEXT, applicationsPage)
        .includeApplications()
        .includeResources())
    )
    .addRoute(ADMINISTRATION_PATH, () => this.#routeToPrivatePath(ADMINISTRATION_PATH, platformContext(ADMINISTRATION_CONTEXT, administrationPage)))
    .addRoute(SLASH, () => redirect(ROUTING_DEFAULT_PATH));

    #sideBar = conditional(this.#authorized).persist(() =>
        sideBar(
            streamsActivator(this.#pagesSwitch), {
                user: this.configuration.user.value,
                themeName: this.configuration.themeName.value
            }
        )
        .useExpanded(expanded => this.configuration.user.cleared(() => expanded.set(false)))
        .onThemeChanged(this.configuration.themeName.set)
    );

    #authorizationPage = () => {
        const page = authorizationPage().authorized(user => this.logIn(user));
        this.configuration.user.cleared(page.clearUser);
        return page;
    };

    #registrationPage = () => {
        const page = registrationPage().registered(user => this.logIn(user));
        this.configuration.user.cleared(page.clearUser);
        return page;
    };

    #mainSwitch = routingSwitch()
    .addRoute(AUTHORIZE_PATH, () => this.#routeToPublicPath(AUTHORIZE_PATH, this.#authorizationPage()))
    .addRoute(REGISTER_PATH, () => this.#routeToPublicPath(REGISTER_PATH, this.#registrationPage()))
    .addNestedRouter(() => this.#routeToPrivatePath(this.#referer, this.#sideBar))
    .addRoute(PLATFORM_PATH, () => redirect(ROUTING_DEFAULT_PATH))
    .addRoute(SLASH, () => redirect(ROUTING_DEFAULT_PATH));

    #content = group().widget(cssBaseLine).widget(browserRouter(this.#mainSwitch));

    constructor() {
        super(Configuration);
        this.onLoad(this.#authenticate);
    }

    logIn = (user: User) => {
        if (this.configuration.user.value) {
            return
        }
        this.lock(() => {
            this.configuration.user.value = user;
            this.redirect(this.#referer)
        })
    }

    logOut = () => {
        Cookies.remove(TOKEN_COOKIE);
        if (!this.configuration.user.value) {
            return
        }
        this.lock(() => {
            this.configuration.user.clear();
            window.location.reload();
        });
    };

    redirect = this.#mainSwitch.redirect;

    location = this.#mainSwitch.location;

    user = () => this.configuration.user.value;

    onUserChanged = (action: Dispatch<User>) => {
        this.configuration.user.consume(action);
        return this;
    }

    setUser = (user: User) => {
        this.configuration.user.value = user
        return this;
    }

    theme = () => THEMES.get(this.themeName())!

    themeName = () => this.configuration.themeName.value;

    onThemeChanged = (action: Dispatch<Theme>) => {
        this.configuration.themeName.consume(name => action(THEMES.get(name)!))
        return this;
    }

    onThemeNameChanged = (action: Dispatch<PlatformTheme>) => {
        this.configuration.themeName.consume(action)
        return this;
    }

    setThemeName = (name: PlatformTheme) => {
        this.configuration.themeName.value = name;
        return this;
    }

    #page = provideTheme(this.theme(),
        provideSnackbar(this.#content,
            {
                anchorOrigin: {
                    vertical: "bottom",
                    horizontal: "center"
                },
                maxSnack: MAX_SNAKES
            }
        )
    )
    .useTheme(theme => this.configuration.themeName.consume(name => theme.set(THEMES.get(name)!)));

    draw = conditional(this.#initialized).persist(() => this.#page).else(this.#loader).render;
}

export const platform = new EntryPoint();
