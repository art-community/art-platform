import {useTheme} from "@material-ui/core";
import {resourceIcon} from "./ResourceIcon";
import {observe} from "../../../framework/pattern/Observable";
import copy from 'copy-to-clipboard';
import {iconChip} from "../../../framework/dsl/simple/SimpleChip";
import {tooltip} from "../../../framework/dsl/simple/SimpleTooltip";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {horizontalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {hooked} from "../../../framework/pattern/Hooked";
import {useNotifications} from "../../../framework/hooks/Hooks";
import {ResourceIdentifier} from "../../../model/ResourceTypes";
import {AssembledArtifact} from "../../../model/AssemblyTypes";

type Properties = {
    artifact: AssembledArtifact
    notClickable?: boolean
}

const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        artifactChip: {
            margin: 5,
            background: "none",
            boxShadow: `0 0 7px ${theme.palette.primary.main}`,
            border: `1px solid ${theme.palette.primary.main}`,
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

export const assembledArtifactIcon = (properties: Properties) =>
    hooked(() => ({
        style: useStyle(),
        notifications: useNotifications()
    }))
    .cache(hooks => {
        const copyArtifact = () => {
            if (!properties.artifact.externalId || properties.notClickable) {
                return
            }
            copy(properties.artifact.externalId.id);
            hooks.notifications.info("Ссылка на артефакт скопирована")
        };

        const style = hooks.style;

        const iconImage = (resourceId: ResourceIdentifier) => iconChip(resourceIcon({
            type: resourceId.type,
            gridContainerStyle: style.resourceIconGrid,
            iconGridItemStyle: style.resourceIconGrid
        }), {
            label: properties.artifact.externalId.id,
            style: style.artifactChip,
            clickable: !properties.notClickable,
            onClick: copyArtifact
        });

        const iconTooltip = (resourceId: ResourceIdentifier) => tooltip({interactive: true})
        .widget(iconImage(resourceId))
        .title(label({text: properties.artifact.externalId.resourceId.name, variant: "subtitle1"}));

        return horizontalGrid({spacing: 1}).pushWidget(iconTooltip(properties.artifact.externalId.resourceId))
    });

export const assembledArtifactsIcons = (artifacts: AssembledArtifact[], clickable?: boolean, iconLabel?: string) => iconLabel
    ? horizontalGrid({
        wrap: "nowrap",
        spacing: 1,
        alignItems: "center"
    })
    .pushWidget(label({noWrap: true, color: "secondary", text: `${iconLabel}:`}))
    .pushWidget(horizontalGrid({spacing: 1}).pushWidgets(artifacts.map(artifact => assembledArtifactIcon({artifact}))))
    : horizontalGrid({spacing: 1}).pushWidgets(artifacts.map(artifact => assembledArtifactIcon({artifact})));
