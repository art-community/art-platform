import {Configurable} from "../../../../framework/pattern/Configurable";
import {event} from "../../../../framework/pattern/Event";
import {Widget} from "../../../../framework/widgets/Widget";
import {Closable} from "../../../../framework/pattern/Optional";
import {button} from "../../../../framework/dsl/managed/ManagedButton";
import {stringSelector} from "../../common/PlatformSelectors";
import {dialog} from "../../../../framework/dsl/managed/ManagedDialog";
import {Dispatch} from "react";
import {verticalGrid} from "../../../../framework/dsl/managed/ManagedGrid";
import {conditional} from "../../../../framework/pattern/Conditional";
import {isNotEmptyArray} from "../../../../framework/extensions/extensions";
import {label} from "../../../../framework/dsl/managed/ManagedLabel";
import {PreparedConfigurationIdentifier} from "../../../../model/PreparedConfigurationTypes";

type Properties = {
    ids: PreparedConfigurationIdentifier[]
    projectId: number
}

class Configuration extends Configurable<Properties> {
    add = event<PreparedConfigurationIdentifier>();
}

class ModulePreparedConfigurationAdditionDialog
    extends Widget<ModulePreparedConfigurationAdditionDialog, Properties, Configuration>
    implements Closable {

    #availableProfiles = () => this.properties.ids
    .filter(id => id.projectId == this.properties.projectId)
    .map(id => id.profile)
    .unique()

    #availableNames = () => {
        const profiles = this.#availableProfiles();
        return this.properties.ids
        .filter(id => profiles.has(id.profile)
            && this.#profileSelector.false() || this.#profileSelector!.get()!.selected() == id.profile
            && id.projectId == this.properties.projectId)
        .map(id => id.name)
        .unique()
    }

    #profileSelector = conditional(() => isNotEmptyArray(this.#availableProfiles()))
    .persist(() => stringSelector({
        label: "Профиль",
        strings: this.properties.ids.map(id => id.profile).unique()
    })
    .onSelect(() => this.#nameSelector.get()?.setAvailableValues(this.#availableNames())))

    #nameSelector = conditional(() => isNotEmptyArray(this.#availableNames()))
    .persist(() => stringSelector({
        label: "Имя",
        strings: this.#availableNames()
    }))

    #button = conditional(() => isNotEmptyArray(this.#availableProfiles()) && isNotEmptyArray(this.#availableNames()))
    .persist(() => button({
        label: "Добавить",
        color: "primary",
        variant: "contained"
    })
    .onClick(() => {
        const id = this.properties
        .ids
        .find(id => id.name == this.#nameSelector.get()?.selected() && id.profile == this.#profileSelector.get()?.selected());
        this.configuration.add.execute(id)
        this.#dialog.close()
    }));

    #content = conditional(() => isNotEmptyArray(this.#availableProfiles()) && isNotEmptyArray(this.#availableNames()))
    .persist(() => verticalGrid({spacing: 1})
        .pushWidget(this.#profileSelector)
        .pushWidget(this.#nameSelector)
    )
    .else(label({text: "Нет доступных конфигураций", color: "secondary"}))

    #dialog = dialog({
        label: "Новая конфигурация",
        maxWidth: "md",
        fullWidth: true,
        visible: true
    })
    .widget(this.#content)
    .action(this.#button, {justify: "flex-end"});

    onClose = this.#dialog.onClose;

    onAdd = (action: Dispatch<PreparedConfigurationIdentifier>) => {
        this.configuration.add.handle(action)
        return this;
    }

    draw = this.#dialog.render;
}

export const modulePreparedCofigurationAdditionDialog = (properties: Properties) =>
    new ModulePreparedConfigurationAdditionDialog(properties, Configuration);
