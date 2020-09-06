import filebeat from "../../../images/filebeat.png";
import {CSSProperties} from "@material-ui/styles";
import {horizontalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {image} from "../../../framework/dsl/simple/SimpleImage";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {empty} from "../../../framework/dsl/simple/SimpleEmptyComponent";
import {FILEBEAT_APPLICATION} from "../../../constants/ApplicationConstants";

type ConcreteApplicationIconProperties = {
    name?: string
    gridContainerStyle?: CSSProperties
    iconGridItemStyle?: CSSProperties
}

type ApplicationIconProperties = ConcreteApplicationIconProperties & {
    type: string
}

export const filebeatIcon = (properties?: ConcreteApplicationIconProperties) => {
    if (!properties?.name) return image({height: 30, width: 39, src: filebeat});

    return horizontalGrid({
        style: properties?.gridContainerStyle,
        wrap: "nowrap",
        spacing: 1,
        alignItems: "center"
    })
    .pushWidget(image({height: 30, width: 39, src: filebeat}), {style: properties?.iconGridItemStyle})
    .pushWidget(label({noWrap: true, color: "primary", text: properties.name}))
};

export const applicationIcon = (properties: ApplicationIconProperties) => {
    switch (properties.type) {
        case FILEBEAT_APPLICATION:
            return filebeatIcon(properties)
    }
    return empty();
};
