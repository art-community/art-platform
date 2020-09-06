import {useTheme} from "@material-ui/core";
import {isEmptyArray, isNotEmptyArray} from "../../../framework/extensions/extensions";
import {observe} from "../../../framework/pattern/Observable";
import {Widget} from "../../../framework/widgets/Widget";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {Configurable} from "../../../framework/pattern/Configurable";
import {hooked} from "../../../framework/pattern/Hooked";
import {styled} from "../../../framework/widgets/Styled";
import {group} from "../../../framework/dsl/simple/SimpleGroup";
import {conditional} from "../../../framework/pattern/Conditional";
import {magicLoader} from "../../embeddable/common/PlatformLoaders";
import {horizontalGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {filter} from "../../embeddable/filter/Filter";
import {divider} from "../../../framework/dsl/simple/SimpleDivider";
import {lazy} from "../../../framework/pattern/Lazy";
import TuneOutlined from "@material-ui/icons/TuneOutlined";
import {proxy} from "../../../framework/widgets/Proxy";
import React from "react";
import {PlatformContext, PlatformContextual} from "../../../context/PlatformContext";
import {tabs} from "../../../framework/dsl/managed/ManagedTabs";
import {Project} from "../../../model/ProjectTypes";
import {PreparedConfigurationIdentifier} from "../../../model/PreparedConfigurationTypes";
import {usePreparedConfigurationApi} from "../../../api/PreparedConfigurationApi";
import {preparedConfigurationEditor} from "../../embeddable/configuration/PreparedConfigurationEditor";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {optional} from "../../../framework/pattern/Optional";
import {preparedConfigurationAdditionDialog} from "../../embeddable/configuration/PeparedConfigurationAdditionDialog";

const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        page: {
            marginLeft: theme.spacing(2),
            marginTop: theme.spacing(2),
        },
        button: {
            marginTop: theme.spacing(2),
        },
        configurations: {
            marginTop: theme.spacing(2),
        },
        editors: {
            marginTop: theme.spacing(2),
        }
    }))
};

class Configuration extends Configurable<PlatformContextual> {
    ids = this.property<PreparedConfigurationIdentifier[]>(this.defaultProperties.context.preparedConfigurations.get)
}

export class ConfigurationsPage extends Widget<ConfigurationsPage, PlatformContextual, Configuration> {
    #filtering = true;

    #api = this.hookValue(usePreparedConfigurationApi);

    #hasProjects = () => isNotEmptyArray(this.properties.context.projects.getInitialized);

    #updateFilter = () => this.#api().getPreparedConfigurationIds(ids => {
        const project = this.properties.context.projects.getInitialized[this.#projectTabs().selected()];
        this.#filter.lock(() => {
            this.#filter
            .setAvailableNames(ids
            .filter(configuration => configuration.projectId == project.id)
            .map(configuration => configuration.name)
            .unique())

            this.#filter
            .setAvailableProfiles(ids
            .filter(configuration => configuration.projectId == project.id)
            .map(configuration => configuration.profile)
            .unique())
        })
        this.#filterConfigurations(false)
    })

    #profiles = () => isEmptyArray(this.#filter.filteredProfiles())
        ? this.#filter.availableProfiles()
        : this.#filter.filteredProfiles();

    #filterConfigurations = (silent: boolean) => {
        const project = this.properties.context.projects.getInitialized[this.#projectTabs().selected()];
        const request = {
            projectIds: [project.id],
            names: this.#filter.filteredNames(),
            profiles: this.#filter.filteredProfiles()
        };
        if (!silent) {
            this.#filtering = true;
            this.#tabs().notify();
        }
        this.#api().getFilteredPreparedConfigurations(request, configurations => this.#refreshEditors(configurations, project, silent));
    }

    #refreshEditors = (ids: PreparedConfigurationIdentifier[], project: Project, silent: boolean) => this.lock(() => {
        this.configuration.ids.value = ids;

        ids.forEach(id => {
            if (!this.#editors.hasKey(id.id)) {
                this.#editors.pushWidget(preparedConfigurationEditor({
                    id,
                    modules: this.properties.context.modules
                })
                .onDelete(this.#updateFilter))
            }
        })

        this.#editors.keys().forEach(key => {
            if (!ids.some(id => id.id == key as number)) {
                this.#editors.removeKey(key)
            }
        })

        this.#editors.arrangeWidgets(ids.map(id => id.id))

        if (!silent) {
            this.#filtering = false;
        }

    });

    #loader = magicLoader(true)

    #noProjectsLabel = label({
        variant: "h5",
        color: "secondary",
        text: "Проекты отсутствуют"
    })

    #editors = verticalGrid({spacing: 1});

    #filter = filter({
        filterNames: true,
        filterProfiles: true
    })
    .onProfilesFiltered(() => this.#filterConfigurations(false))
    .onNamesFiltered(() => this.#filterConfigurations(false));

    #projectTabs = lazy(() => tabs({
            variant: "scrollable",
            labels: this.properties.context.projects.getInitialized.map(project => project.name)
        })
        .onSelect(this.#updateFilter)
    );

    #button = button({
        label: "Добавить конфигурацию",
        variant: "contained",
        color: "primary"
    })
    .onClick(() => this.#additionDialog.spawn());

    #tabs = lazy(() => group()
    .widget(hooked(useStyle).cache(style => styled(this.#button, style.button)))
    .widget(hooked(useStyle).widget(style => styled(group().widget(this.#projectTabs())
        .widget(divider())
        .widget(this.#filter)
        .widget(hooked(useStyle)
            .widget(style => styled(conditional(() => !this.#filtering)
            .persist(() => horizontalGrid({spacing: 1}).breakpoints({xs: true})
                .pushWidgets(this.#profiles()
                .filter(profile => isNotEmptyArray(this.configuration.ids.value.filter(id => id.profile == profile)))
                .map(profile => verticalGrid({spacing: 1})
                    .pushWidget(label({
                        text: `Профиль ${profile}`,
                        variant: "h5",
                        color: "primary"
                    }))
                    .pushWidget(divider(1, 1))
                    .pushWidgets(this.configuration.ids.value
                        .filter(id => id.profile == profile).map(id => this.#editors.getWidgetByKey(id.id))
                    )
                ))
            )
            .else(this.#loader), style.editors))
        ), style.configurations))
    ));

    #content = conditional(this.#hasProjects).persist(this.#tabs).else(this.#noProjectsLabel);

    #page = hooked(useStyle).widget(style => styled(group()
        .widget(horizontalGrid({spacing: 1, alignItems: "center"})
            .pushWidget(proxy(<TuneOutlined fontSize={"large"} color={"secondary"}/>))
            .pushWidget(label({variant: "h3", color: "primary", text: "Конфигурации"}))
        )
        .widget(this.#content), style.page)
    );

    #additionDialog = this.add(
        optional(() => {
            const project = this.properties.context.projects.getInitialized[this.#projectTabs().selected()];
            return preparedConfigurationAdditionDialog(this.configuration.ids.value, project)
        })
        .onDestroy(this.#updateFilter)
    );

    constructor(properties: PlatformContextual) {
        super(properties, Configuration);
        this.#hasProjects() && this.onLoad(() => this.#updateFilter());
    }

    draw = this.#page.render;
}

export const configurationsPage = (context: PlatformContext) => new ConfigurationsPage({context})
