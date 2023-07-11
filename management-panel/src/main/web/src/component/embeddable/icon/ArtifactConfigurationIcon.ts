import {useTheme} from "@material-ui/core";
import {resourceIcon} from "./ResourceIcon";
import {observe} from "../../../framework/pattern/Observable";
import {tooltip} from "../../../framework/dsl/simple/SimpleTooltip";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {horizontalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {iconChip} from "../../../framework/dsl/simple/SimpleChip";
import {hooked} from "../../../framework/pattern/Hooked";
import {ResourceIdentifier} from "../../../model/ResourceTypes";
import {ArtifactConfiguration} from "../../../model/AssemblyTypes";

type Properties = {
    name: string
    resourceIds: ResourceIdentifier[]
}

const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        artifactChip: {
            margin: 5,
            background: "none",
            boxShadow: `0 0 7px ${theme.palette.secondary.main}`,
            border: `1px solid ${theme.palette.secondary.main}`,
            paddingTop: 30,
            paddingBottom: 30,
            paddingLeft: 10,
            color: theme.palette.primary.main
        },
        resourceIconGrid: {
            width: "auto"
        }
    }));
};

export const artifactConfigurationIcon = (properties: Properties) =>
    hooked(useStyle)
    .cache(style => {
        const iconImage = (resourceId: ResourceIdentifier) => iconChip(resourceIcon({
            type: resourceId.type,
            gridContainerStyle: style.resourceIconGrid,
            iconGridItemStyle: style.resourceIconGrid
        }), {label: properties.name, style: style.artifactChip});

        const iconTooltip = (resourceId: ResourceIdentifier) => tooltip({interactive: true})
        .widget(iconImage(resourceId))
        .title(label({text: resourceId.name}));

        return horizontalGrid({spacing: 1}).pushWidgets(properties.resourceIds.map(iconTooltip))
    });

export const artifactConfigurationIcons = (configurations: ArtifactConfiguration[], iconLabel?: string) => iconLabel
    ? horizontalGrid({
        wrap: "nowrap",
        spacing: 1,
        alignItems: "center"
    })
    .pushWidget(label({noWrap: true, color: "secondary", text: `${iconLabel}:`}))
    .pushWidget(horizontalGrid({spacing: 1}).pushWidgets(configurations.map(configuration => artifactConfigurationIcon({
        name: configuration.artifact!.name,
        resourceIds: configuration.archives.map(archive => archive.resourceId) || []
    }))))

    : horizontalGrid({spacing: 1}).pushWidgets(configurations.map(configuration => artifactConfigurationIcon({
        name: configuration.artifact!.name,
        resourceIds: configuration.archives.map(archive => archive.resourceId) || []
    })));
