import {Configurable} from "../../../../framework/pattern/Configurable";
import {event} from "../../../../framework/pattern/Event";
import {Widget} from "../../../../framework/widgets/Widget";
import {Closable} from "../../../../framework/pattern/Optional";
import {button} from "../../../../framework/dsl/managed/ManagedButton";
import {dialog} from "../../../../framework/dsl/managed/ManagedDialog";
import {Dispatch} from "react";
import {when} from "../../../../framework/pattern/When";
import {FILEBEAT_APPLICATION} from "../../../../constants/ApplicationConstants";
import {ModuleApplication} from "../../../../model/ModuleTypes";
import {ModuleApplicationConfigurator} from "./ModuleApplicationsCollection";

type Factory = () => ModuleApplicationConfigurator<any>;

type Properties = {
    application: ModuleApplication
    filebeatFactory: Factory
}

class Configuration extends Configurable<Properties> {
    edit = event<ModuleApplication>();
}

class ModuleApplicationEditingDialog
    extends Widget<ModuleApplicationEditingDialog, Properties, Configuration>
    implements Closable {

    #button = button({
        label: "Сохранить",
        color: "primary",
        variant: "contained"
    })
    .onClick(() => {
        this.configuration.edit.execute({
            applicationId: this.properties.application.applicationId,
            application: this.#currentConfigurator()!.configure()
        })
        this.#dialog.close()
    });

    #configurator = when()
    .persist(
        () => this.properties.application.applicationId.type == FILEBEAT_APPLICATION,
        () => this.properties.filebeatFactory().onChange(() => this.#validate())
    )

    #dialog = dialog({
        label: `Приложение ${this.properties.application.applicationId.name}`,
        maxWidth: "md",
        fullWidth: true,
        visible: true
    })
    .widget(this.#configurator)
    .action(this.#button, {justify: "flex-end"});

    #currentConfigurator = () => this.#configurator.current<ModuleApplicationConfigurator<any>>();

    #validate = () => {
        this.#button.setDisabled(!this.#currentConfigurator()?.isValid())
    }

    onClose = this.#dialog.onClose;

    onEdit = (action: Dispatch<ModuleApplication>) => {
        this.configuration.edit.handle(action)
        return this;
    }

    constructor(properties: Properties) {
        super(properties, Configuration);
        this.onLoad(this.#validate)
    }

    draw = this.#dialog.render;
}

export const moduleApplicationEditingDialog = (properties: Properties) => new ModuleApplicationEditingDialog(properties);
