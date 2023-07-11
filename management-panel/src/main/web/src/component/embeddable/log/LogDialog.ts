import {DispatchWithoutAction} from "react";
import {useTheme} from "@material-ui/core";
import {observe} from "../../../framework/pattern/Observable";
import {Widget} from "../../../framework/widgets/Widget";
import {dialog} from "../../../framework/dsl/managed/ManagedDialog";
import {hooked} from "../../../framework/pattern/Hooked";
import {logViewer} from "./LogViewer";
import {PlatformTheme} from "../../../constants/PlatformTheme";

type Properties = {
    label: string
    loading: boolean
}

const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        backgroundColor: theme.palette.type == PlatformTheme.DARK
            ? "black"
            : "white",
        color: theme.palette.type == PlatformTheme.DARK
            ? "white"
            : "black"
    }))
};

export class LogDialog extends Widget<LogDialog, Properties> {
    #viewer = logViewer({
        loading: this.properties.loading
    });

    #dialog = hooked(useStyle).cache(style => dialog({
        contentStyle: style,
        label: this.properties.label,
        maxWidth: "xl",
        fullWidth: true,
        visible: true
    })
    .widget(this.#viewer));

    onClose = (action: DispatchWithoutAction) => {
        this.#dialog.apply(dialog => dialog.onClose(action))
        return this;
    }

    setLoading = (loading: boolean) => {
        this.#viewer.setLoading(loading)
        return this;
    }

    setRecords = (records: string[]) => {
        this.#viewer.setRecords(records)
        return this;
    }


    draw = this.#dialog.render;
}

export const logDialog = (properties: Properties) => new LogDialog(properties);
