import {text} from "../../../framework/dsl/managed/ManagedTextField";
import {dialog} from "../../../framework/dsl/managed/ManagedDialog";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {ENTITY_NAME_REGEX} from "../../../constants/Regexps";
import {Closable} from "../../../framework/pattern/Optional";
import {ResourceIdentifier} from "../../../model/ResourceTypes";
import {verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {StaticWidget} from "../../../framework/widgets/Widget";
import {ApplicationIdentifier} from "../../../model/ApplicationTypes";
import {FILEBEAT_APPLICATION} from "../../../constants/ApplicationConstants";
import {filebeatApplicationFields} from "./filebeat/FilebeatApplicationFields";
import {useApplicationApi} from "../../../api/ApplicationsApi";
import {FilebeatApplication} from "../../../model/FilebeatTypes";
import {ApplicationField} from "./ApplicationField";
import {form} from "../../../framework/widgets/Form";

type ApplicationType = (FilebeatApplication) & { type: string };

type Properties = {
    application: ApplicationType
    resourceIds: ResourceIdentifier[]
}

class ApplicationEditingDialog extends StaticWidget<ApplicationEditingDialog, Properties> implements Closable {
    #applicationIds: ApplicationIdentifier[] = [];
    #api = this.hookValue(useApplicationApi);

    #createFields = () => {
        switch (this.properties.application.type) {
            case FILEBEAT_APPLICATION:
                return filebeatApplicationFields(this.properties.resourceIds);
        }
        return new Map<string, ApplicationField<unknown>>();
    }

    #fields = this.#createFields();

    #validate = () => {
        this.#validateDuplicates();
        const empties = !this.#name.text() || this.#fields.some((name, value) => !value.value());
        const errors = this.#name.error() || this.#fields.some((name, value) => value.error());
        const disabled = empties || errors;
        this.#button.setDisabled(disabled)
    };

    #validateDuplicates = () => {
        const {name, type} = this.properties.application;
        if (!this.#applicationIds.some(id => id.type == type && id.name == this.#name.text() && id.name != name)) {
            return;
        }
        this.#name.setError({
            error: true,
            text: "Приложение с таким именем уже существует"
        })
    };

    #updateApplication = () => {
        const api = this.#api();
        let properties = {};
        this.#fields.forEach((field, name) => properties[name] = field.value())
        const application = {id: this.properties.application.id, name: this.#name.text(), ...properties};
        switch (this.properties.application.type) {
            case FILEBEAT_APPLICATION:
                api.updateFilebeatApplication(application as FilebeatApplication, this.#dialog.close);
                return;
        }
    };

    #button = button({
        color: "primary",
        variant: "contained",
        disabled: true,
        label: "Сохранить"
    })
    .onClick(this.#updateApplication)
    .onClick(() => this.#dialog.close());

    #name = text({
        label: "Имя",
        value: this.properties.application.name,
        fullWidth: true,
        required: true,
        placeholder: "my-application",
        autoFocus: true,
        regexp: ENTITY_NAME_REGEX,
        defaultErrorText: "Имя приложения не должно быть пустым и содержать только символы [0-9a-z-.]"
    })
    .onTextChanged(this.#validate);

    #dialog = dialog({
        label: "Изменить приложение",
        visible: true,
        fullWidth: true
    })
    .widget(form(verticalGrid({spacing: 2})
        .pushWidget(this.#name)
        .pushWidgets(this.#fields.valuesToArray(field => field.widget)))
    )
    .actions([this.#button], {justify: "flex-end"})
    .onClose(() => {
        this.#name.clear()
        this.#fields.forEach(value => value.clear())
    });

    constructor(properties: Properties) {
        super(properties);

        this.#fields.forEach((field, name) => field.setValue(this.properties.application[name]).onChange(this.#validate))

        this.onLoad(() => this.#api().getApplicationIds(ids => {
            this.#applicationIds = ids
            this.#validateDuplicates();
        }));
    }

    onClose = this.#dialog.onClose;

    draw = this.#dialog.render;
}

export const applicationEditingDialog = (resourceIds: ResourceIdentifier[], application: ApplicationType) => new ApplicationEditingDialog({resourceIds, application});
