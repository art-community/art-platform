import {useTheme} from "@material-ui/core";
import {isNotEmptyArray} from "../../../framework/extensions/extensions";
import {buildLauncherDialog} from "../../embeddable/assembly/BuildLauncherDialog";
import {observe} from "../../../framework/pattern/Observable";
import {Widget} from "../../../framework/widgets/Widget";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {Configurable} from "../../../framework/pattern/Configurable";
import {hooked} from "../../../framework/pattern/Hooked";
import {styled} from "../../../framework/widgets/Styled";
import {group} from "../../../framework/dsl/simple/SimpleGroup";
import {conditional} from "../../../framework/pattern/Conditional";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {AssemblyInformation} from "../../../model/AssemblyTypes";
import {magicLoader} from "../../embeddable/common/PlatformLoaders";
import {useAssemblyApi} from "../../../api/AssemblyApi";
import {horizontalGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {onAnyAssemblyUpdated, onAssemblyAdded, onAssemblyDeleted} from "../../../streams/AssemblyStream";
import {assemblyCard} from "../../embeddable/assembly/AssemblyCard";
import {filter} from "../../embeddable/filter/Filter";
import {divider} from "../../../framework/dsl/simple/SimpleDivider";
import {lazy} from "../../../framework/pattern/Lazy";
import {MAX_ASSEMBLIES} from "../../../constants/AssemblyConstants";
import LoopOutlined from "@material-ui/icons/LoopOutlined";
import {proxy} from "../../../framework/widgets/Proxy";
import React from "react";
import {PlatformContext, PlatformContextual} from "../../../context/PlatformContext";
import {tabs} from "../../../framework/dsl/managed/ManagedTabs";
import {optional} from '../../../framework/pattern/Optional';
import {Project} from "../../../model/ProjectTypes";

const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        page: {
            marginLeft: theme.spacing(2),
            marginTop: theme.spacing(2),
        },
        assemblies: {
            marginTop: theme.spacing(2),
        },
        tabs: {
            marginTop: theme.spacing(1),
        },
        cards: {
            marginTop: theme.spacing(2),
        }
    }))
};

class Configuration extends Configurable<PlatformContextual> {
    assemblies = this.property<AssemblyInformation[]>(this.defaultProperties.context.assemblies.get)
}

export class AssembliesPage extends Widget<AssembliesPage, PlatformContextual, Configuration> {
    #filtering = true;

    #assemblyApi = this.hookValue(useAssemblyApi);

    #hasResources = () => isNotEmptyArray(this.properties.context.resources.ids());

    #hasProjects = () => isNotEmptyArray(this.properties.context.projects.getInitialized);

    #hasAssemblies = () => isNotEmptyArray(this.configuration.assemblies.value);

    #updateFilter = () => {
        const project = this.properties.context.projects.getInitialized[this.#projectTabs().selected()];
        this.#filter.setAvailableVersions(project.versions.flatMap(version => version.version))
        this.#filterAssemblies(false)
    }

    #filterAssemblies = (silent: boolean) => {
        const project = this.properties.context.projects.getInitialized[this.#projectTabs().selected()];
        const request = {
            projectIds: [project.id],
            versions: this.#filter.filteredVersions(),
            count: MAX_ASSEMBLIES,
            sorted: true
        };
        if (!silent) {
            this.#filtering = true;
            this.#tabs().notify();
        }
        this.#assemblyApi().getFilteredAssemblies(request, assemblies => this.#refreshCards(assemblies, project, silent));
    }

    #refreshCards = (assemblies: AssemblyInformation[], project: Project, silent: boolean) => this.lock(() => {
        this.configuration.assemblies.value = assemblies;

        assemblies.forEach(assembly => {
            if (!this.#cards.hasKey(assembly.id)) {
                this.#cards.pushWidget(assemblyCard({
                    project,
                    assembly,
                    projects: this.properties.context.projects.getInitialized,
                    resources: this.properties.context.resources.store(),
                    applications: this.properties.context.applications.store(),
                    preparedConfigurations: this.properties.context.preparedConfigurations.get,
                }))
            }
        })

        this.#cards.keys().forEach(key => {
            if (!assemblies.some(assembly => assembly.id == key as number)) {
                this.#cards.removeKey(key)
            }
        })

        if (!silent) {
            this.#cards.arrangeWidgets(assemblies.map(assembly => assembly.id))
            this.#filtering = false;
        }
    });

    #loader = magicLoader(true)

    #noResourcesLabel = label({
        variant: "h5",
        color: "secondary",
        text: "Ресурсы отсутствуют"
    })

    #noProjectsLabel = label({
        variant: "h5",
        color: "secondary",
        text: "Проекты отсутствуют"
    })

    #noAssembliesLabel = label({
        variant: "h5",
        color: "secondary",
        text: "Сборки отсутствуют"
    })

    #buildLauncher = this.add(optional(() => buildLauncherDialog({
        projects: this.properties.context.projects.getInitialized,
        resources: this.properties.context.resources.store()
    })))

    #buildButton = conditional(() => this.#hasProjects() && this.#hasResources())
    .persist(() => button({
        variant: "contained",
        color: "primary",
        label: "Запустить сборку"
    })
    .onClick(this.#buildLauncher.spawn));

    #cards = verticalGrid({spacing: 2}).breakpoints({xs: true});

    #filter = filter({
        filterVersions: true
    })
    .onVersionsFiltered(() => this.#filterAssemblies(false));

    #projectTabs = lazy(() => tabs({
            variant: "scrollable",
            labels: this.properties.context.projects.getInitialized.map(project => project.name)
        })
        .onSelect(this.#updateFilter)
    );

    #tabs = lazy(() => group()
    .widget(this.#projectTabs())
    .widget(divider())
    .widget(this.#filter)
    .widget(hooked(useStyle).widget(style => styled(conditional(() => !this.#filtering)
        .persist(() => conditional(this.#hasAssemblies)
            .persist(() => this.#cards)
            .else(this.#noAssembliesLabel)
        )
        .else(this.#loader),
        style.cards))
    ));

    #content = conditional(this.#hasResources)
    .persist(() => hooked(useStyle).cache(style => styled(group()
        .widget(this.#buildButton)
        .widget(styled(conditional(this.#hasProjects)
        .persist(this.#tabs)
        .else(this.#noProjectsLabel), style.assemblies)), style.assemblies))
    )
    .else(this.#noResourcesLabel);

    #page = hooked(useStyle).widget(style => styled(group()
        .widget(horizontalGrid({spacing: 1, alignItems: "center"})
            .pushWidget(proxy(<LoopOutlined fontSize={"large"} color={"secondary"}/>))
            .pushWidget(label({variant: "h3", color: "primary", text: "Сборки"}))
        )
        .widget(this.#content), style.page)
    );

    constructor(properties: PlatformContextual) {
        super(properties, Configuration);

        this.#hasProjects() && this.onLoad(() => this.#updateFilter());

        this.subscribe(() => onAssemblyAdded(() => this.#filterAssemblies(true)))
        .subscribe(() => onAnyAssemblyUpdated(() => this.#filterAssemblies(true)))
        .subscribe(() => onAssemblyDeleted(() => this.#filterAssemblies(true)));
    }

    draw = this.#page.render;
}

export const assembliesPage = (context: PlatformContext) => new AssembliesPage({context})
