import {useTheme} from "@material-ui/core";
import {isNotEmptyArray} from "../../../framework/extensions/extensions";
import {observe} from "../../../framework/pattern/Observable";
import {Widget} from "../../../framework/widgets/Widget";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {Configurable} from "../../../framework/pattern/Configurable";
import {hooked} from "../../../framework/pattern/Hooked";
import {styled} from "../../../framework/widgets/Styled";
import {group} from "../../../framework/dsl/simple/SimpleGroup";
import {conditional} from "../../../framework/pattern/Conditional";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {magicLoader} from "../../embeddable/common/PlatformLoaders";
import {horizontalGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {filter} from "../../embeddable/filter/Filter";
import {divider} from "../../../framework/dsl/simple/SimpleDivider";
import {lazy} from "../../../framework/pattern/Lazy";
import {ModuleInformation} from "../../../model/ModuleTypes";
import {useModuleApi} from "../../../api/ModuleApi";
import {moduleCard} from "../../embeddable/module/ModuleCard";
import {modulesInstallationDialog} from "../../embeddable/module/ModulesInstallationDialog";
import {onModuleAdded, onModuleDeleted} from "../../../streams/ModuleStream";
import {panel} from "../../../framework/dsl/managed/ManagedPanel";
import {moduleIsChanging} from "../../../service/ModuleService";
import {MODULE_FILTERABLE_STATES} from "../../../constants/ModuleStateConstants";
import {chip} from "../../../framework/dsl/simple/SimpleChip";
import ViewModule from "@material-ui/icons/ViewModule";
import {proxy} from "../../../framework/widgets/Proxy";
import React from "react";
import {PlatformContext, PlatformContextual} from "../../../context/PlatformContext";
import {tabs} from "../../../framework/dsl/managed/ManagedTabs";
import {optional} from "../../../framework/pattern/Optional";
import SystemUpdateAltOutlined from "@material-ui/icons/SystemUpdateAltOutlined";
import {MODULE_RUN_STATE} from "../../../constants/States";

const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        page: {
            marginLeft: theme.spacing(2),
            marginTop: theme.spacing(2),
        },
        modules: {
            marginTop: theme.spacing(2),
        },
        tabs: {
            marginTop: theme.spacing(1),
        },
        cards: {
            marginTop: theme.spacing(2),
        },
        countChip: {
            marginRight: theme.spacing(2)
        }
    }))
};

class Configuration extends Configurable<PlatformContextual> {
    modules = this.property<ModuleInformation[]>(this.defaultProperties.context.modules.get)
}

export class ModulesPage extends Widget<ModulesPage, PlatformContextual, Configuration> {
    #filtering = true;

    #expandedVersions = new Map<string, boolean>()

    #moduleApi = this.hookValue(useModuleApi);

    #hasResources = () => isNotEmptyArray(this.properties.context.resources.ids());

    #hasProjects = () => isNotEmptyArray(this.properties.context.projects.getInitialized);

    #hasModules = () => isNotEmptyArray(this.configuration.modules.value);

    #updateFilter = (silent: boolean) => {
        const project = this.properties.context.projects.getInitialized[this.#projectTabs().selected()];
        const request = {projectIds: [project.id], sorted: true};
        this.#filter.setAvailableVersions(project.versions.flatMap(version => version.version))
        this.#moduleApi().getFilteredModules(request, modules => {
            this.#filter.setAvailableModules(modules)
            modules.filter(moduleIsChanging).forEach(module => this.#expandedVersions.set(module.artifact.version, true))
            this.#filterModules(silent);
        });
    }

    #filterModules = (silent: boolean) => {
        const project = this.properties.context.projects.getInitialized[this.#projectTabs().selected()];
        const request = {
            projectIds: [project.id],
            versions: this.#filter.filteredVersions(),
            ids: this.#filter.filteredModules().map(module => module.id),
            states: this.#filter.filteredStates(),
            sorted: true
        };
        if (!silent) {
            this.#filtering = true;
            this.#tabs().notify();
        }
        this.#moduleApi().getFilteredModules(request, modules => this.#refreshCards(modules, project, silent));
    }

    #refreshCards = (modules: ModuleInformation[], project, silent: boolean) => this.lock(() => {
        this.configuration.modules.value = modules;

        modules.forEach(module => {
            if (!this.#cards.hasKey(module.id)) {
                this.#cards.pushWidget(moduleCard({
                    project,
                    module,
                    applications: this.properties.context.applications.store(),
                    resources: this.properties.context.resources.store(),
                    modules: this.#filter.availableModules(),
                    preparedConfigurations: this.properties.context.preparedConfigurations.get
                }))
            }
        })

        this.#cards.keys().forEach(key => {
            if (!modules.some(module => module.id == key as number)) {
                this.#cards.removeKey(key)
            }
        })

        if (!silent) {
            this.#cards.arrangeWidgets(modules.map(module => module.id))
            this.#filtering = false;
        }
    });

    #getVersions = () => this.properties.context.projects.getInitialized[this.#projectTabs().selected()]
    .versions
    .filter(version => this.configuration.modules.value!.some(module => module.artifact.version == version.version));

    #getModuleCardsByVersion = (version: string) => this.#cards
    .filter(moduleId => this.configuration.modules.value!.some(module => module.id == moduleId && module.artifact.version == version));

    #modules = () => verticalGrid({spacing: 1, wrap: "nowrap"})
    .pushWidgets(this.#getVersions().map(version => panel(this.#getModuleCardsByVersion(version.version), {
        summaryLeftWidget: hooked(useStyle).cache(style => styled(chip({
            label: this.configuration.modules.value!.count(module => module.artifact.version == version.version),
            color: "secondary"
        }), style.countChip)),
        summaryRightWidget: horizontalGrid({spacing: 1}).pushWidget(button({
                tooltip: "Обновить все",
                icon: proxy(<SystemUpdateAltOutlined color={"primary"}/>)
            })
            .onClick(() => this.configuration.modules.value!
            .filter(module => module.state == MODULE_RUN_STATE && module.artifact.version == version.version)
            .forEach(module => this.#moduleApi().refreshModuleArtifact(module.id)))
        ),
        label: version.version,
        expanded: this.#expandedVersions.get(version.version)
    })
    .onExpansionChanged(expanded => this.#expandedVersions.set(version.version, expanded))))

    #loader = magicLoader(true);

    #noResourcesLabel = label({
        variant: "h5",
        color: "secondary",
        text: "Ресурсы отсутствуют"
    })

    #noEntitiesLabel = label({
        variant: "h5",
        color: "secondary",
        text: this.#hasProjects() ? "Модули отсутствуют" : "Проекты отсутствуют"
    })

    #moduleInstaller = this.add(optional(() => modulesInstallationDialog({
        projects: this.properties.context.projects.getInitialized,
        applications: this.properties.context.applications.store(),
        resources: this.properties.context.resources.store(),
        preparedConfigurations: this.properties.context.preparedConfigurations.get
    })))

    #installButton = conditional(() => this.#hasProjects() && this.#hasResources())
    .persist(() => button({
        variant: "contained",
        color: "primary",
        label: "Установить модули"
    })
    .onClick(this.#moduleInstaller.spawn));

    #cards = verticalGrid({spacing: 2}).breakpoints({xs: true});

    #filter = filter({
        filterVersions: true,
        filterModules: true,
        filterStates: true,
        states: MODULE_FILTERABLE_STATES
    })
    .onModulesFiltered(() => this.#filterModules(false))
    .onStatesFiltered(() => this.#filterModules(false))
    .onVersionsFiltered(() => this.#filterModules(false));

    #projectTabs = lazy(() => tabs({
            variant: "scrollable",
            labels: this.properties.context.projects.getInitialized.map(project => project.name)
        })
        .onSelect(() => this.#updateFilter(false))
    );

    #tabs = lazy(() => group()
    .widget(this.#projectTabs())
    .widget(divider())
    .widget(this.#filter)
    .widget(hooked(useStyle).widget(style => styled(conditional(() => !this.#filtering)
        .persist(() => conditional(this.#hasModules)
            .widget(this.#modules)
            .else(this.#noEntitiesLabel)
        )
        .else(this.#loader), style.tabs))
    ));

    #content = conditional(this.#hasResources)
    .persist(() => hooked(useStyle)
        .cache(style => styled(group()
        .widget(this.#installButton)
        .widget(styled(conditional(this.#hasProjects)
        .persist(this.#tabs)
        .else(this.#noEntitiesLabel), style.modules)), style.modules))
    )
    .else(this.#noResourcesLabel);

    #page = hooked(useStyle).widget(style => styled(group()
        .widget(horizontalGrid({spacing: 1, alignItems: "center"})
            .pushWidget(proxy(<ViewModule fontSize={"large"} color={"secondary"}/>))
            .pushWidget(label({variant: "h3", color: "primary", text: "Модули"}))
        )
        .widget(this.#content), style.page)
    );

    constructor(properties: PlatformContextual) {
        super(properties, Configuration);

        this.#hasProjects() && this.onLoad(() => this.#updateFilter(false))

        this.subscribe(() => onModuleAdded(() => this.#updateFilter(true))).subscribe(() => onModuleDeleted(() => this.#updateFilter(true)));
    }

    draw = this.#page.render;
}

export const modulesPage = (context: PlatformContext) => new ModulesPage({context})
