import {Chip, Grid, Typography, useTheme} from "@material-ui/core";
import * as React from "react";
import {observe} from "../../../framework/pattern/Observable";
import {avatar} from "../../../framework/dsl/simple/SimpleAvatar";
import {User} from "../../../model/UserTypes";
import {Configurable} from "../../../framework/pattern/Configurable";
import {hooked} from "../../../framework/pattern/Hooked";
import {Widget} from "../../../framework/widgets/Widget";

type Properties = {
    user: User
    fullView?: boolean
}

export const useStyle = (fullView?: boolean) => {
    const theme = useTheme();
    return observe(theme, fullView).render(() => ({
        avatar: {
            background: theme.palette.primary.main
        },
        chip: {
            opacity: fullView ? 1 : 0
        },
        userName: {
            marginLeft: theme.spacing(0.5),
            marginRight: theme.spacing(0.5)
        }
    }));
};

class Configuration extends Configurable<Properties> {
    fullView = this.property(this.defaultProperties.fullView);
}

class UserAvatar extends Widget<UserAvatar, Properties, Configuration> {
    #userAvatar = hooked(() => useStyle(this.configuration.fullView.value))
    .cache(style => avatar(this.properties.user.name[0].toUpperCase(), {style: style.avatar}));

    setFullView = (value: boolean) => {
        this.configuration.fullView.value = value;
        return this;
    };

    draw = () => {
        const style = useStyle(this.configuration.fullView.value);

        const name = observe(this.properties.user.name, style).render(() =>
            <Grid item>
                <div style={style.userName}>
                    <Typography style={style.chip} noWrap color={"primary"}>
                        {this.properties.user.name}
                    </Typography>
                </div>
            </Grid>
        );

        return <Grid alignItems={"center"} container wrap={"nowrap"} direction={"row"} spacing={1}>
            <Grid item>
                {this.#userAvatar.render()}
            </Grid>
            <Grid direction={"column"} container spacing={1}>
                {name}
                {this.properties.user.admin && <Grid item><Chip style={style.chip} label={"Администратор"} color={"primary"}/></Grid>}
            </Grid>
        </Grid>
    }
}

export const userAvatar = (properties: Properties) => new UserAvatar(properties, Configuration);
