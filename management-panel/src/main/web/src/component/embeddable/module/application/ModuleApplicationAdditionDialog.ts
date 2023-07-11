import {Configurable} from "../../../../framework/pattern/Configurable";
import {event} from "../../../../framework/pattern/Event";
import {Widget} from "../../../../framework/widgets/Widget";
import {Closable} from "../../../../framework/pattern/Optional";
import {button} from "../../../../framework/dsl/managed/ManagedButton";
import {applicationSelector} from "../../common/PlatformSelectors";
import {ApplicationIdentifier} from "../../../../model/ApplicationTypes";
import {dialog} from "../../../../framework/dsl/managed/ManagedDialog";
import {Dispatch} from "react";
import {when} from "../../../../framework/pattern/When";
import {FILEBEAT_APPLICATION} from "../../../../constants/ApplicationConstants";
import {verticalGrid} from "../../../../framework/dsl/managed/ManagedGrid";
import {ModuleApplication} from "../../../../model/ModuleTypes";
import {conditional} from "../../../../framework/pattern/Conditional";
import {isNotEmptyArray} from "../../../../framework/extensions/extensions";
import {label} from "../../../../framework/dsl/managed/ManagedLabel";
import {ModuleApplicationConfigurator} from "./ModuleApplicationsCollection";

type Factory = (id: ApplicationIdentifier) => ModuleApplicationConfigurator<any>;

type Properties = {
    applicationIds: ApplicationIdentifier[]
    filebeatFactory: Factory
}

class Configuration extends Configurable<Properties> {
    add = event<ModuleApplication>();
}

class ModuleApplicationAdditionDialog
    extends Widget<ModuleApplicationAdditionDialog, Properties, Configuration>
    implements Closable {

    #selector = conditional(() => isNotEmptyArray(this.properties.applicationIds))
    .persist(() => applicationSelector({ids: this.properties.applicationIds}))

    #button = conditional(() => isNotEmptyArray(this.properties.applicationIds))
    .persist(() => button({
        label: "Добавить",
        color: "primary",
        variant: "contained"
    })
    .onClick(() => {
        this.configuration.add.execute({
            applicationId: this.#selector.get()!.selected(),
            application: this.#currentConfigurator()!.configure()
        })
        this.#dialog.close()
    }));

    #configurator = when()
    .persist(
        () => this.#selector.get()?.selected()?.type == FILEBEAT_APPLICATION,
        () => this.properties.filebeatFactory(this.#selector.get()!.selected()).onChange(() => this.#validate())
    )

    #content = conditional(() => isNotEmptyArray(this.properties.applicationIds))
    .persist(() => verticalGrid({spacing: 1})
        .pushWidget(this.#selector.apply(selector => selector.onSelect(() => this.#configurator.notify())))
        .pushWidget(this.#configurator)
    )
    .else(label({text: "Нет доступных приложений", color: "secondary"}))

    #dialog = dialog({
        label: "Новое приложение",
        maxWidth: "md",
        fullWidth: true,
        visible: true
    })
    .widget(this.#content)
    .action(this.#button, {justify: "flex-end"});

    #currentConfigurator = () => this.#configurator.current<ModuleApplicationConfigurator<any>>();

    #validate = () => {
        this.#button.get()?.setDisabled(!this.#currentConfigurator()?.isValid())
    }

    onClose = this.#dialog.onClose;

    onAdd = (action: Dispatch<ModuleApplication>) => {
        this.configuration.add.handle(action)
        return this;
    }

    constructor(properties: Properties) {
        super(properties, Configuration);
        this.onLoad(this.#validate)
    }

    draw = this.#dialog.render;
}

export const moduleApplicationAdditionDialog = (properties: Properties) => new ModuleApplicationAdditionDialog(properties);
