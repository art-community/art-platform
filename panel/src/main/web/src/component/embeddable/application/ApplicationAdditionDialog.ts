import {text} from "../../../framework/dsl/managed/ManagedTextField";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {ENTITY_NAME_REGEX} from "../../../constants/Regexps";
import {Closable} from "../../../framework/pattern/Optional";
import {ResourceIdentifier} from "../../../model/ResourceTypes";
import {verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {dialog} from "../../../framework/dsl/managed/ManagedDialog";
import {StaticWidget} from "../../../framework/widgets/Widget";
import {filebeatApplicationFields} from "./filebeat/FilebeatApplicationFields";
import {useApplicationApi} from "../../../api/ApplicationsApi";
import {FILEBEAT_APPLICATION} from "../../../constants/ApplicationConstants";
import {FilebeatApplication} from "../../../model/FilebeatTypes";
import {ApplicationField} from "./ApplicationField";
import {form} from "../../../framework/widgets/Form";

type Properties = {
    type: string
    resourceIds: ResourceIdentifier[]
}

class ApplicationAdditionDialog extends StaticWidget<ApplicationAdditionDialog, Properties> implements Closable {
    #applicationIds: ResourceIdentifier[] = [];
    #api = this.hookValue(useApplicationApi);

    #createFields = () => {
        switch (this.properties.type) {
            case FILEBEAT_APPLICATION:
                return filebeatApplicationFields(this.properties.resourceIds);
        }
        return new Map<string, ApplicationField<unknown>>();
    }

    #fields = this.#createFields()

    #validate = () => {
        this.#validateDuplicates();
        const empties = !this.#name.text() || this.#fields.some((name, value) => !value.value());
        const errors = this.#name.error() || this.#fields.some((name, value) => value.error());
        const disabled = empties || errors;
        this.#button.setDisabled(disabled)
    };

    #validateDuplicates = () => {
        if (!this.#applicationIds.some(id => id.type == this.properties.type && id.name == this.#name.text())) {
            return;
        }
        this.#name.setError({
            error: true,
            text: "Приложение с таким именем уже существует"
        })
    };

    #addApplication = () => {
        const api = this.#api();
        let properties = {};
        this.#fields.forEach((field, name) => properties[name] = field.value())
        const application = {name: this.#name.text(), ...properties};
        switch (this.properties.type) {
            case FILEBEAT_APPLICATION:
                api.addFilebeatApplication(application as FilebeatApplication, this.#dialog.close);
                return;
        }
    };

    #button = button({
        color: "primary",
        variant: "contained",
        disabled: true,
        label: "Добавить"
    })
    .onClick(this.#addApplication)
    .onClick(() => this.#dialog.close());

    #name = text({
        label: "Имя",
        fullWidth: true,
        required: true,
        placeholder: "my-application",
        autoFocus: true,
        regexp: ENTITY_NAME_REGEX,
        defaultErrorText: "Имя приложения не должно быть пустым и содержать только символы [0-9a-z-.]"
    })
    .onTextChanged(this.#validate);

    #dialogLabel = () => {
        switch (this.properties.type) {
            case FILEBEAT_APPLICATION:
                return "Новый Filebeat"
        }
    }

    #dialog = dialog({
        label: this.#dialogLabel(),
        visible: true,
        fullWidth: true
    })
    .widget(form(verticalGrid({spacing: 1})
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

        this.#fields.forEach(field => field.onChange(this.#validate))

        this.onLoad(() => this.#api().getApplicationIds(ids => {
            this.#applicationIds = ids;
            this.#validateDuplicates();
        }));
    }

    onClose = this.#dialog.onClose;

    draw = this.#dialog.render;
}

export const applicationAdditionDialog = (resourceIds: ResourceIdentifier[], type: string) => new ApplicationAdditionDialog({resourceIds, type});
