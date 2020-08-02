import {labelChip} from "../../../framework/dsl/simple/SimpleChip";
import {horizontalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {label} from "../../../framework/dsl/managed/ManagedLabel";

export const versionIcon = (version: string) => labelChip(version, {color: "primary"});

export const versionIcons = (versions: string[], iconLabel?: string) => iconLabel
    ? horizontalGrid({
        wrap: "nowrap",
        spacing: 1,
        alignItems: "center"
    })
    .pushWidget(label({noWrap: true, color: "secondary", text: `${iconLabel}:`}))
    .pushWidget(horizontalGrid({spacing: 1}).pushWidgets(versions.map(versionIcon)))

    : horizontalGrid({spacing: 1}).pushWidgets(versions.map(versionIcon));
