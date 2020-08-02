import openShift from "../../../images/open-shift.png";
import artifacts from "../../../images/artifacts.png";
import git from "../../../images/git.png";
import proxy from "../../../images/proxy.png";
import {ARTIFACTS_RESOURCE, GIT_RESOURCE, OPEN_SHIFT_RESOURCE, PLATFORM_RESOURCE, PROXY_RESOURCE} from "../../../constants/ResourceConstants";
import {CSSProperties} from "@material-ui/styles";
import {horizontalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {image} from "../../../framework/dsl/simple/SimpleImage";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {empty} from "../../../framework/dsl/simple/SimpleEmptyComponent";

type ConcreteResourceIconProperties = {
    name?: string
    gridContainerStyle?: CSSProperties
    iconGridItemStyle?: CSSProperties
}

type ResourceIconProperties = ConcreteResourceIconProperties & {
    type: string
}

export const openShiftResourceIcon = (properties?: ConcreteResourceIconProperties) => {
    if (!properties?.name) return image({height: 30, width: 39, src: openShift});
    return horizontalGrid({
        style: properties?.gridContainerStyle,
        wrap: "nowrap",
        spacing: 1,
        alignItems: "center"
    })
    .pushWidget(image({height: 30, width: 39, src: openShift}), {style: properties?.iconGridItemStyle})
    .pushWidget(label({noWrap: true, color: "primary", text: properties?.name}), {
        style: properties?.iconGridItemStyle
    });
};

export const artifactsResourceIcon = (properties?: ConcreteResourceIconProperties) => {
    if (!properties?.name) return image({height: 35, width: 35, src: artifacts});
    return horizontalGrid({
        style: properties?.gridContainerStyle,
        wrap: "nowrap",
        spacing: 1,
        alignItems: "center"
    })
    .pushWidget(image({height: 35, width: 35, src: artifacts}), {style: properties?.iconGridItemStyle})
    .pushWidget(label({noWrap: true, color: "primary", text: properties?.name}), {
        style: properties?.iconGridItemStyle
    });
};

export const gitResourceIcon = (properties?: ConcreteResourceIconProperties) => {
    if (!properties?.name) return image({height: 35, width: 35, src: git});
    return horizontalGrid({
        style: properties?.gridContainerStyle,
        wrap: "nowrap",
        spacing: 1,
        alignItems: "center"
    })
    .pushWidget(image({height: 35, width: 35, src: git}), {style: properties?.iconGridItemStyle})
    .pushWidget(label({noWrap: true, color: "primary", text: properties?.name}), {
        style: properties?.iconGridItemStyle
    })
};

export const proxyResourceIcon = (properties?: ConcreteResourceIconProperties) => {
    if (!properties?.name) return image({height: 35, width: 35, src: proxy});
    return horizontalGrid({
        style: properties?.gridContainerStyle,
        wrap: "nowrap",
        spacing: 1,
        alignItems: "center"
    })
    .pushWidget(image({height: 35, width: 35, src: proxy}), {style: properties?.iconGridItemStyle})
    .pushWidget(label({noWrap: true, color: "primary", text: properties?.name}), {
        style: properties?.iconGridItemStyle
    })
};

export const platformResourceIcon = (properties?: ConcreteResourceIconProperties) => empty()

export const resourceIcon = (properties: ResourceIconProperties) => {
    switch (properties.type) {
        case OPEN_SHIFT_RESOURCE:
            return openShiftResourceIcon(properties);
        case ARTIFACTS_RESOURCE:
            return artifactsResourceIcon(properties);
        case GIT_RESOURCE:
            return gitResourceIcon(properties);
        case PLATFORM_RESOURCE:
            return platformResourceIcon(properties);
        case PROXY_RESOURCE:
            return proxyResourceIcon(properties)
    }
    return empty();
};
