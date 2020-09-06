import * as React from "react";
import {Link, Typography, useTheme} from "@material-ui/core";
import {RESOURCES_PATH} from "../../../constants/Routers";
import {observe} from "../../../framework/pattern/Observable";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {proxy} from "../../../framework/widgets/Proxy";
import {horizontalGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {Widget} from "../../../framework/widgets/Widget";
import {Configurable} from "../../../framework/pattern/Configurable";
import {group} from "../../../framework/dsl/simple/SimpleGroup";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {isNotEmptyArray} from "../../../framework/extensions/extensions";
import {onProjectAdded, onProjectDeleted} from "../../../streams/ProjectStream";
import {hooked} from "../../../framework/pattern/Hooked";
import {styled} from "../../../framework/widgets/Styled";
import {conditional} from "../../../framework/pattern/Conditional";
import {projectAdditionDialog} from "../../embeddable/project/ProjectAdditionDialog";
import {projectCard} from '../../embeddable/project/ProjectCard';
import {Project} from "../../../model/ProjectTypes";
import {SELF} from "../../../framework/constants/Constants";
import FeaturedPlayList from "@material-ui/icons/FeaturedPlayList";
import {PlatformContext, PlatformContextual} from "../../../context/PlatformContext";
import {optional} from "../../../framework/pattern/Optional";

const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        page: {
            marginLeft: theme.spacing(2),
            marginTop: theme.spacing(2),
        },
        cards: {
            marginTop: theme.spacing(2),
        }
    }))
};

class Configuration extends Configurable<PlatformContextual> {
    projects = this.property<Project[]>(this.defaultProperties.context.projects.get)
}

class ProjectsPage extends Widget<ProjectsPage, PlatformContextual, Configuration> {
    #hasResources = () => isNotEmptyArray(this.properties.context.resources.get.openShift) && isNotEmptyArray(this.properties.context.resources.get.git);

    #hasProjects = () => isNotEmptyArray(this.configuration.projects.value);

    #createProjectCards = (projects: Project[]) => {
        this.configuration.projects.value = projects;
        const resourcesStore = this.properties.context.resources.store();
        this.#cards.pushWidgets(projects.map(project => projectCard(project, projects, resourcesStore)));
    }

    #stubLabel = proxy(
        <>
            <Typography variant={"h6"} color={"secondary"}>
                Для начала работы с проектами вам нужны <Link href={RESOURCES_PATH} target={SELF}>ресурсы</Link>
            </Typography>
        </>
    );

    #addProjectButton = button({
        variant: "contained",
        color: "primary",
        label: "Добавить проект"
    })
    .onClick(() => this.#additionDialog.spawn());

    #cards = verticalGrid({spacing: 1}).breakpoints({xs: true});

    #projects = conditional(this.#hasResources)
    .persist(() => group()
        .widget(this.#addProjectButton)
        .widget(conditional(this.#hasProjects).persist(() => hooked(useStyle).cache(style => styled(this.#cards, style.cards))))
    )
    .else(this.#stubLabel);

    #additionDialog = this.add(optional(() => projectAdditionDialog(this.properties.context.resources.store())));

    #page = hooked(useStyle).widget(style => styled(group()
        .widget(horizontalGrid({spacing: 1, alignItems: "center"})
            .pushWidget(proxy(<FeaturedPlayList fontSize={"large"} color={"secondary"}/>))
            .pushWidget(label({color: "primary", variant: "h3", text: "Проекты"}))
        )
        .widget(styled(this.#projects, style.cards)), style.page)
    );

    constructor(properties: PlatformContextual) {
        super(properties, Configuration);
        this.#createProjectCards(properties.context.projects.get)
        this.subscribe(() => onProjectAdded(project => this.lock(() => {
            this.configuration.projects.value = [...this.configuration.projects.value, project]
            const resourcesStore = this.properties.context.resources.store();
            this.#cards.pushWidget(projectCard(project, this.configuration.projects.value, resourcesStore))
        })))
        .subscribe(() => onProjectDeleted(project => this.lock(() => {
            this.configuration.projects.value = this.configuration.projects.value!.filter(current => current.id != project.id)
            this.#cards.removeKey(project.id)
        })));
    }

    draw = this.#page.render;
}

export const projectsPage = (context: PlatformContext) => new ProjectsPage({context});
