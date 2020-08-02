import {labelChip} from "../../../framework/dsl/simple/SimpleChip";
import {horizontalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {PortMapping} from "../../../model/ModuleTypes";
import {Chip, Grid, Typography} from "@material-ui/core";
import React from 'react';
import {proxy} from "../../../framework/widgets/Proxy";

export const portIcon = (port: number) => labelChip(port.toString(), {color: "primary"});

export const portIcons = (ports: number[], iconLabel?: string) => iconLabel
    ? horizontalGrid({
        wrap: "nowrap",
        spacing: 1,
        alignItems: "center"
    })
    .pushWidget(label({noWrap: true, color: "secondary", text: `${iconLabel}:`}))
    .pushWidget(horizontalGrid({spacing: 1}).pushWidgets(ports.map(portIcon)))

    : horizontalGrid({spacing: 1}).pushWidgets(ports.map(portIcon));

export const portMappingIcon = (port: PortMapping) => proxy(
    <Grid key={port.externalPort.toString()} item container spacing={2} alignItems={"center"}
          wrap={"nowrap"}>
        <Grid item>
            <Chip label={port.internalPort} color={"primary"}/>
        </Grid>
        <Grid item>
            <Typography color={"primary"} noWrap>доступен через</Typography>
        </Grid>
        <Grid item>
            <Chip label={port.externalPort} color={"primary"}/>
        </Grid>
    </Grid>
);

export const portMappingIcons = (ports: PortMapping[]) => proxy(
    <Grid container spacing={1}>
        {ports.map(port => portMappingIcon(port).render())}
    </Grid>
);
