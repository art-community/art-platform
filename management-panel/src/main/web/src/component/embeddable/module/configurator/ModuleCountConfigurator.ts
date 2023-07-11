import {DispatchWithoutAction} from "react";
import {Widget} from "../../../../framework/widgets/Widget";
import {useTheme} from "@material-ui/core";
import {observe} from "../../../../framework/pattern/Observable";
import {MODULE_MIN_COUNT} from "../../../../constants/ModuleConstants";
import {verticalGrid} from "../../../../framework/dsl/managed/ManagedGrid";
import {label} from "../../../../framework/dsl/managed/ManagedLabel";
import {Configurator} from "../../../../framework/pattern/Configurator";
import {text} from "../../../../framework/dsl/managed/ManagedTextField";
import {POSITIVE_NUMBER_REGEX} from "../../../../constants/Regexps";

const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        slider: {
            marginLeft: theme.spacing(2),
            marginRight: theme.spacing(2)
        }
    }))
};

type Properties = {
    count?: number
}

export class ModuleCountConfigurator extends Widget<ModuleCountConfigurator, Properties> implements Configurator<number> {
    #counter = text({
        label: "Количество",
        regexp: POSITIVE_NUMBER_REGEX,
        mask: POSITIVE_NUMBER_REGEX,
        fullWidth: true,
        value: this.properties.count?.toString() || MODULE_MIN_COUNT,
    })
    .useText(text => text.prevent(value => !value?.startsWith("0")));

    #configurator = verticalGrid({spacing: 1, wrap: "nowrap"})
    .pushWidget(label({color: "secondary", variant: "h6", noWrap: true, text: "Количество экземпляров"}))
    .pushWidget(this.#counter)

    onChange = (action: DispatchWithoutAction) => {
        this.#counter.onTextChanged(action)
        return this;
    }

    configure = () => Number(this.#counter.text()) || MODULE_MIN_COUNT

    draw = this.#configurator.render;
}

export const moduleCountConfigurator = (count?: number) => new ModuleCountConfigurator({count})
