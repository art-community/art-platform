import {ManagedSelector, selector} from "../../../framework/dsl/managed/ManagedSelector";
import {applicationIcon} from "../icon/ApplicationIcon";
import {JDK_VERSIONS} from "../../../constants/JavaConstants";
import {projectIcon} from "../icon/ProjectIcon";
import {technologyOf} from "../../../constants/TechnologyConstants";
import {labeledTechnologyIcon, technologyIcon} from "../icon/TechnologyIcons";
import {GRADLE_VERSIONS} from "../../../constants/GradleConstants";
import {resourceIcon} from "../icon/ResourceIcon";
import {assembledArtifactIcon} from "../icon/AssembledArtifactIcons";
import {ResourceAttribute} from "../../../model/ResourceProperty";
import {ApplicationIdentifier} from "../../../model/ApplicationTypes";
import {ResourceIdentifier} from "../../../model/ResourceTypes";
import {Project} from "../../../model/ProjectTypes";
import {AssembledArtifact} from "../../../model/AssemblyTypes";

export type ApplicationSelectorProperties = {
    ids: ApplicationIdentifier[]
    label?: string
    selected?: ApplicationIdentifier
}
export type StringSelectorProperties = {
    strings: string[]
    selected?: string
    label: string
}
export type JdkVersionSelectorProperties = {
    selected?: string
    label?: string
}
export type GradleVersionSelectorProperties = {
    selected?: string
    label?: string
}
export type ProjectSelectorProperties = {
    projects: Project[]
    selected?: Project
    label?: string
}
export type TechnologySelectorProperties = {
    technologies: string[]
    selected?: string
    label?: string
}
export type ResourceSelectorProperties = {
    ids: ResourceIdentifier[]
    label?: string
    selected?: ResourceIdentifier
}
export type ResourceAttributeSelectorProperties = {
    attributes: ResourceAttribute[]
    selected?: ResourceAttribute
    label?: string
}
export type AssembledArtifactSelectorProperties = {
    artifacts: AssembledArtifact[]
    selected?: AssembledArtifact
    label?: string
}

export type ApplicationSelector = ManagedSelector<ApplicationIdentifier>;
export type StringSelector = ManagedSelector<string>;
export type JdkVersionSelector = ManagedSelector<string>;
export type GradleVersionSelector = ManagedSelector<string>;
export type ProjectSelector = ManagedSelector<Project>;
export type TechnologySelector = ManagedSelector<string>;
export type ResourceSelector = ManagedSelector<ResourceIdentifier>;
export type ResourceAttributeSelector = ManagedSelector<ResourceAttribute>;
export type AssembledArtifactSelector = ManagedSelector<AssembledArtifact>;

export const stringSelector = (properties: StringSelectorProperties) => selector<string>({
    label: {
        text: properties.label
    },
    itemFactory: version => ({
        option: version,
        value: version,
        suggestion: version
    }),
    available: properties.strings,
    selected: properties.selected
});

export const jdkVersionSelector = (properties: JdkVersionSelectorProperties) => selector<string>({
    label: {
        text: properties.label || "Версия JDK"
    },
    itemFactory: version => ({
        option: version,
        value: version,
        suggestion: version
    }),
    available: JDK_VERSIONS,
    selected: properties.selected
});

export const gradleVersionSelector = (properties: GradleVersionSelectorProperties) => selector<string>({
    label: {
        text: properties.label || "Версия Gradle"
    },
    itemFactory: version => ({
        option: version,
        value: version,
        suggestion: version
    }),
    available: GRADLE_VERSIONS,
    selected: properties.selected
});

export const projectSelector = (properties: ProjectSelectorProperties) => selector<Project>({
    label: {
        text: properties.label || "Проект"
    },
    itemFactory: project => ({
        option: projectIcon(project.name),
        value: project,
        suggestion: project.name
    }),
    available: properties.projects,
    selected: properties.selected
});

export const technologySelector = (properties: TechnologySelectorProperties) => selector<string>({
    label: {
        text: properties.label || "Технология"
    },
    itemFactory: name => ({
        option: labeledTechnologyIcon(name, technologyOf(name).descriptionLabel),
        suggestion: technologyOf(name).descriptionLabel,
        value: name,
        icon: technologyIcon(name)
    }),
    available: properties.technologies,
    selected: properties.selected
});

export const resourceSelector = (properties: ResourceSelectorProperties) => selector<ResourceIdentifier>({
    label: {
        text: properties.label || "Ресурс"
    },
    itemFactory: id => ({
        option: resourceIcon({name: id.name, type: id.type}),
        suggestion: id.name,
        value: id,
        icon: resourceIcon({type: id.type})
    }),
    available: properties.ids,
    selected: properties.selected
});

export const applicationSelector = (properties: ApplicationSelectorProperties) => selector<ApplicationIdentifier>({
    label: {
        text: properties.label || "Приложение"
    },
    itemFactory: id => ({
        option: applicationIcon({name: id.name, type: id.type}),
        suggestion: id.name,
        value: id,
        icon: applicationIcon({type: id.type})
    }),
    available: properties.ids,
    selected: properties.selected
})

export const resourceAttributeSelector = (properties: ResourceAttributeSelectorProperties) => selector<ResourceAttribute>({
    label: {
        text: properties.label || "Атрибут"
    },
    itemFactory: attribute => ({
        option: attribute.name,
        value: attribute,
        suggestion: attribute.name
    }),
    available: properties.attributes,
    selected: properties.selected
});

export const assembledArtifactSelector = (properties: AssembledArtifactSelectorProperties) => selector<AssembledArtifact>({
    label: {
        text: properties.label || "Артефакт"
    },
    itemFactory: artifact => ({
        option: assembledArtifactIcon({artifact, notClickable: true}),
        suggestion: artifact.externalId.id,
        value: artifact
    }),
    available: properties.artifacts,
    selected: properties.selected
});
