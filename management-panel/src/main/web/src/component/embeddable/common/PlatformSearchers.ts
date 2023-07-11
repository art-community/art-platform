import {Searcher, searcher, searcherItem} from "../../../framework/dsl/managed/ManagedSearcher";
import {observe} from "../../../framework/pattern/Observable";
import {useTheme} from "@material-ui/core";
import {Hooked, hooked} from "../../../framework/pattern/Hooked";
import {ResourceIdentifier} from "../../../model/ResourceTypes";
import {Project, ProjectArtifact} from "../../../model/ProjectTypes";
import {AssembledArtifact} from "../../../model/AssemblyTypes";
import {technologiesOf, Technology} from "../../../constants/TechnologyConstants";
import {resourceIcon} from "../icon/ResourceIcon";
import {labeledTechnologyIcon} from "../icon/TechnologyIcons";
import {projectIcon} from "../icon/ProjectIcon";
import {ModuleInformation} from "../../../model/ModuleTypes";
import {moduleIcon} from "../icon/ModuleIcon";

export const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        chip: {
            margin: theme.spacing(0.5),
            background: "none",
            boxShadow: `0 0 7px ${theme.palette.primary.main}`,
            border: `1px solid ${theme.palette.primary.main}`,
            padding: 20,
            paddingLeft: 1,
            color: theme.palette.primary.main
        },
    }));
};

export type ResourceSearcherProperties = {
    ids: ResourceIdentifier[]
    selected?: ResourceIdentifier[]
    label?: string
}
export type ProjectSearcherProperties = {
    projects: Project[]
    selected?: Project[]
    label?: string
}
export type StringSearcherProperties = {
    strings: string[]
    label: string
    selected?: string[]
}
export type TechnologySearcherProperties = {
    technologies: string[]
    selected?: string[]
    label?: string
}
export type ProjectArtifactSearcherProperties = {
    artifacts: ProjectArtifact[]
    disableCloseOnSelect?: boolean
    selected?: ProjectArtifact[]
    label?: string
}
export type AssembledArtifactsSearcherProperties = {
    artifacts: AssembledArtifact[]
    selected?: AssembledArtifact[]
    label?: string
}
export type ModuleSearcherProperties = {
    modules: ModuleInformation[]
    selected?: ModuleInformation[]
    label?: string
}

export type ResourceSearcher = Hooked<ReturnType<typeof useStyle>, Searcher<ResourceIdentifier>>
export type ProjectSearcher = Hooked<ReturnType<typeof useStyle>, Searcher<Project>>
export type StringSearcher = Hooked<ReturnType<typeof useStyle>, Searcher<string>>
export type TechnologySearcher = Hooked<ReturnType<typeof useStyle>, Searcher<string>>
export type ProjectArtifactsSearcher = Hooked<ReturnType<typeof useStyle>, Searcher<ProjectArtifact>>
export type AssembledArtifactsSearcher = Hooked<ReturnType<typeof useStyle>, Searcher<AssembledArtifact>>
export type ModuleSearcher = Hooked<ReturnType<typeof useStyle>, Searcher<ModuleInformation>>

export const resourceSearcher = (properties: ResourceSearcherProperties) => hooked(useStyle)
.cache<Searcher<ResourceIdentifier>>((style, current) =>
    searcher<ResourceIdentifier>({
        label: {
            text: properties.label || "Ресурсы"
        },
        selected: current?.selected() || properties.selected,
        itemFactory: id => searcherItem<ResourceIdentifier>({
            option: resourceIcon({name: id.name, type: id.type}),
            tag: {
                widget: resourceIcon({name: id.name, type: id.type, gridContainerStyle: {margin: style.chip.margin}}),
                chipped: true,
                chipProperties: {
                    style: style.chip
                }
            },
            value: id,
            suggestion: id.name
        }),
        available: properties.ids
    })
);

export const projectSearcher = (properties: ProjectSearcherProperties) => hooked(useStyle)
.cache<Searcher<Project>>((style, current) =>
    searcher<Project>({
        label: {
            text: properties.label || "Проекты"
        },
        selected: current?.selected() || properties.selected,
        itemFactory: project => searcherItem<Project>({
            option: projectIcon(project.name),
            tag: {
                widget: projectIcon(project.name),
                chipped: true,
                chipProperties: {
                    style: style.chip
                }
            },
            value: project,
            suggestion: project.name
        }),
        available: properties.projects
    })
);

export const stringSearcher = (properties: StringSearcherProperties) => hooked(useStyle)
.cache<Searcher<string>>((style, current) =>
    searcher<string>({
        label: {
            text: properties.label
        },
        selected: current?.selected() || properties.selected,
        itemFactory: string => searcherItem<string>({
            option: string,
            tag: {
                widget: string,
                chipped: true,
                chipProperties: {
                    style: style.chip
                }
            },
            value: string,
            suggestion: string
        }),
        available: properties.strings
    })
);

export const technologySearcher = (properties: TechnologySearcherProperties) => hooked(useStyle)
.cache<Searcher<Technology>>((style, current) =>
    searcher<Technology>({
        label: {
            text: properties.label || "Технологии"
        },
        selected: current?.selected() || properties.selected ? technologiesOf(properties.selected!) : [],
        itemFactory: technology => ({
            option: labeledTechnologyIcon(technology.name, technology.descriptionLabel),
            tag: {
                widget: labeledTechnologyIcon(technology.name, technology.descriptionLabel, true),
                chipped: true,
                chipProperties: {
                    style: style.chip
                }
            },
            value: technology,
            suggestion: technology.name
        }),
        available: technologiesOf(properties.technologies)
    })
);

export const projectArtifactsSearcher = (properties: ProjectArtifactSearcherProperties) => hooked(useStyle)
.cache<Searcher<ProjectArtifact>>((style, current) =>
    searcher<ProjectArtifact>({
        disableCloseOnSelect: properties.disableCloseOnSelect,
        label: {
            text: properties.label || "Артефакты проекта"
        },
        selected: current?.selected() || properties.selected,
        itemFactory: artifact => ({
            option: artifact.name,
            tag: {
                widget: artifact.name,
                chipped: true,
                chipProperties: {
                    style: style.chip
                },
                labelProperties: {
                    variant: "subtitle1",
                    style: {padding: 5}
                }
            },
            value: artifact,
            suggestion: artifact.name
        }),
        available: properties.artifacts,
        comparator: (current, other) => current.name == other.name
    })
);

export const assembledArtifactsSearcher = (properties: AssembledArtifactsSearcherProperties) => hooked(useStyle)
.cache<Searcher<AssembledArtifact>>((style, current) =>
    searcher<AssembledArtifact>({
        label: {
            text: properties.label || "Артефакты"
        },
        selected: current?.selected() || properties.selected,
        itemFactory: artifact => ({
            option: artifact.name,
            tag: {
                widget: artifact.name,
                chipped: true,
                chipProperties: {
                    style: style.chip
                },
                labelProperties: {
                    variant: "subtitle1",
                    style: {padding: 5}
                }
            },
            value: artifact,
            suggestion: artifact.name
        }),
        available: properties.artifacts
    })
);

export const moduleSearcher = (properties: ModuleSearcherProperties) => hooked(useStyle)
.cache<Searcher<ModuleInformation>>((style, current) =>
    searcher<ModuleInformation>({
        label: {
            text: properties.label || "Модули"
        },
        selected: current?.selected() || properties.selected,
        itemFactory: module => searcherItem<ModuleInformation>({
            option: moduleIcon(module.name),
            tag: {
                widget: moduleIcon(module.name),
                chipped: true,
                chipProperties: {
                    style: style.chip
                }
            },
            value: module,
            suggestion: module.name
        }),
        available: properties.modules
    })
);
