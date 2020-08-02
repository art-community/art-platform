import {text} from "../../../framework/dsl/managed/ManagedTextField";
import {LABEL_REGEX} from "../../../constants/Regexps";
import {dialog} from "../../../framework/dsl/managed/ManagedDialog";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {Closable} from "../../../framework/pattern/Optional";
import {Widget} from "../../../framework/widgets/Widget";
import {Configurable} from "../../../framework/pattern/Configurable";
import {event} from "../../../framework/pattern/Event";
import {Dispatch} from "react";
import {handleEnter} from "../../../framework/constants/Constants";

type Properties = {
    excludeNames: string[]
}

class Configuration extends Configurable<Properties> {
    add = event<string>();
}

class ArtifactsGroupAdditionDialog extends Widget<ArtifactsGroupAdditionDialog, Properties, Configuration> implements Closable {
    #validate = (name: string) => {
        const duplicate = this.properties.excludeNames.includes(name);
        const error = !this.#name.text() || this.#name.error() || duplicate;
        if (duplicate) {
            this.#name.setError({error: true, text: "Группа с таким именем уже задана"});
        }
        this.#button.setDisabled(error)
    }

    #button = button({
        label: "Добавить",
        color: "primary",
        variant: "contained",
        disabled: true
    })
    .onClick(() => {
        this.#dialog.close()
        this.configuration.add.execute(this.#name.text())
    });

    #name = text({
        label: "Назовите группу",
        placeholder: "Backend",
        regexp: LABEL_REGEX,
        fullWidth: true,
        autoFocus: true
    })
    .onTextChanged(this.#validate);

    #dialog = dialog({
        label: "Новая группа артефактов",
        maxWidth: "md",
        fullWidth: true,
        visible: true,
        onKeyDown: handleEnter(() => this.#button.click())
    })
    .widget(this.#name)
    .action(this.#button, {justify: "flex-end"});

    onClose = this.#dialog.onClose;

    onAdd = (action: Dispatch<string>) => {
        this.configuration.add.handle(action)
        return this;
    }

    draw = this.#dialog.render;
}

export const artifactsGroupAdditionDialog = (excludeNames: string[]) => new ArtifactsGroupAdditionDialog({excludeNames}, Configuration);
