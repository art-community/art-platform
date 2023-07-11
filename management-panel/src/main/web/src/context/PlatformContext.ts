import {applicationsLoader} from "../loader/ApplicationsLoader";
import {resourcesLoader} from "../loader/ResourcesLoader";
import {projectsLoader} from "../loader/ProjectsLoader";
import {modulesLoader} from "../loader/ModulesLoader";
import {assembliesLoader} from "../loader/AssembliesLoader";
import {Widget} from "../framework/widgets/Widget";
import {useApplicationApi} from "../api/ApplicationsApi";
import {useProjectApi} from "../api/ProjectApi";
import {useResourceApi} from "../api/ResourceApi";
import {useAssemblyApi} from "../api/AssemblyApi";
import {useModuleApi} from "../api/ModuleApi";
import {lazy} from "../framework/pattern/Lazy";
import {conditional} from "../framework/pattern/Conditional";
import {magicLoader} from "../component/embeddable/common/PlatformLoaders";
import {Synchronizer} from "../framework/pattern/Synchronizer";
import {usePreparedConfigurationApi} from "../api/PreparedConfigurationApi";
import {preparedConfigurationsLoader} from "../loader/PreparedConfigurationsLoader";

type WidgetFactory = (context: PlatformContext) => Widget<any>;

enum State {
    CREATED,
    LOADING,
    INITIALIZED
}

export type PlatformContextual = {
    context: PlatformContext
}

export class PlatformContext extends Widget<PlatformContext> {
    #loader = magicLoader(true)

    #state = State.CREATED;

    #widgetFactory: WidgetFactory;

    #applicationApi = this.hookValue(useApplicationApi)
    #projectApi = this.hookValue(useProjectApi)
    #resourceApi = this.hookValue(useResourceApi)
    #assemblyApi = this.hookValue(useAssemblyApi)
    #moduleApi = this.hookValue(useModuleApi)
    #preparedConfigurationApi = this.hookValue(usePreparedConfigurationApi)

    #applicationsLoader = lazy(() => applicationsLoader(this.#applicationApi))
    #assembliesLoader = lazy(() => assembliesLoader(this.#assemblyApi))
    #modulesLoader = lazy(() => modulesLoader(this.#moduleApi))
    #projectsLoader = lazy(() => projectsLoader(this.#projectApi))
    #resourcesLoader = lazy(() => resourcesLoader(this.#resourceApi))
    #preparedConfigurationsLoader = lazy(() => preparedConfigurationsLoader(this.#preparedConfigurationApi))

    #withApplications = false
    #withAssemblies = false
    #withModules = false
    #withProjects = false
    #withResources = false
    #withPreparedConfigurations = false

    includeApplications = () => {
        this.#withApplications = true
        return this
    }

    includeAssemblies = () => {
        this.#withAssemblies = true
        return this
    }

    includeModules = () => {
        this.#withModules = true
        return this
    }

    includeProjects = () => {
        this.#withProjects = true
        return this
    }

    includeResources = () => {
        this.#withResources = true
        return this
    }

    includePreparedConfigurations = () => {
        this.#withPreparedConfigurations = true
        return this
    }

    reload = () => {
        this.synchronizer.reset()
        this.#state = State.CREATED;
        return this;
    }

    get resources() {
        return this.#resourcesLoader()
    }

    get applications() {
        return this.#applicationsLoader()
    }

    get modules() {
        return this.#modulesLoader()
    }

    get projects() {
        return this.#projectsLoader()
    }

    get assemblies() {
        return this.#assembliesLoader()
    }

    get preparedConfigurations() {
        return this.#preparedConfigurationsLoader()
    }

    constructor(widgetFactory: WidgetFactory) {
        super();
        this.#widgetFactory = widgetFactory;

        const action = (synchronizer: Synchronizer) => {
            if (this.#state == State.INITIALIZED || this.#state == State.LOADING) {
                return synchronizer;
            }

            this.#state = State.LOADING;

            synchronizer.onComplete(() => this.#state = State.INITIALIZED)

            if (this.#withApplications) {
                synchronizer.action(() => this.#applicationsLoader().load(synchronizer.process))
            }

            if (this.#withAssemblies) {
                synchronizer.action(() => this.#assembliesLoader().load(synchronizer.process))
            }

            if (this.#withModules) {
                synchronizer.action(() => this.#modulesLoader().load(synchronizer.process))
            }

            if (this.#withProjects) {
                synchronizer.action(() => this.#projectsLoader().load(synchronizer.process))
            }

            if (this.#withResources) {
                synchronizer.action(() => this.#resourcesLoader().load(synchronizer.process))
            }

            if (this.#withPreparedConfigurations) {
                synchronizer.action(() => this.#preparedConfigurationsLoader().load(synchronizer.process))
            }

            return synchronizer
        };

        this.synchronize(this.onRender, action)
    }

    draw = conditional(() => this.#state == State.INITIALIZED)
    .widget(() => this.#widgetFactory(this))
    .else(this.#loader).render
}

export const platformContext = (name: string, widgetFactory: WidgetFactory) => {
    const context = new PlatformContext(widgetFactory);
    platformContexts.set(name, context)
    return context;
}

export const reloadContexts = () => platformContexts.forEach(context => context.reload())

export const reloadContext = (name: string) => platformContexts.get(name)?.reload()

const platformContexts: Map<string, PlatformContext> = new Map()
