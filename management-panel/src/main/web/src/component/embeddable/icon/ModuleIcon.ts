import {useTheme} from "@material-ui/core";
import {observe} from "../../../framework/pattern/Observable";
import {horizontalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {avatar} from "../../../framework/dsl/simple/SimpleAvatar";
import {hooked} from "../../../framework/pattern/Hooked";

const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        container: {
            margin: theme.spacing(0.5)
        },
        textAvatar: {
            backgroundColor: theme.palette.primary.main
        }
    }));
};

export const moduleIcon = (name: string) => hooked(useStyle).cache(style =>
    horizontalGrid({wrap: "nowrap", spacing: 1, alignItems: "center", style: style.container})
    .pushWidget(avatar(name[0].toUpperCase(), {style: style.textAvatar}))
    .pushWidget(label({text: name, color: "primary"})));
