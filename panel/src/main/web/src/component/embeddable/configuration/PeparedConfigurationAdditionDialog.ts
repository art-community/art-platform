import {text} from "../../../framework/dsl/managed/ManagedTextField";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {Closable} from "../../../framework/pattern/Optional";
import {verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {dialog} from "../../../framework/dsl/managed/ManagedDialog";
import {StaticWidget} from "../../../framework/widgets/Widget";
import {form} from "../../../framework/widgets/Form";
import {PreparedConfigurationIdentifier} from "../../../model/PreparedConfigurationTypes";
import {CONFIG_FILE_NAME_REGEX, ENTITY_NAME_REGEX} from "../../../constants/Regexps";
import {CONFIGURATION_FILE_FORMATS} from "../../../constants/ConfigurationConstants";
import {usePreparedConfigurationApi} from "../../../api/PreparedConfigurationApi";
import {codeEditor} from "../../../framework/dsl/managed/ManagedCodeEditor";
import {platform} from "../../entry/EntryPoint";
import {PlatformTheme} from "../../../constants/PlatformTheme";
import {CodeEditorTheme, DEFAULT_CODE_EDITOR_HEIGHT, DEFAULT_CODE_EDITOR_WIDTH} from "../../../framework/constants/Constants";
import {Project} from "../../../model/ProjectTypes";
import {useNotifications} from "../../../framework/hooks/Hooks";

type Properties = {
    ids: PreparedConfigurationIdentifier[]
    project: Project
}

class PreparedConfigurationAdditionDialog extends StaticWidget<PreparedConfigurationAdditionDialog, Properties> implements Closable {
    #notifications = this.hookValue(useNotifications)

    #api = this.hookValue(usePreparedConfigurationApi);

    #validate = () => {
        this.#validateDuplicates();
        const empties = !this.#name.text() || !this.#profile.text() || !this.#content.text();
        const errors = this.#name.error() || this.#profile.error();
        const disabled = empties || errors;
        this.#button.setDisabled(disabled)
    };

    #validateDuplicates = () => {
        const duplicate = this.properties.ids.some(id => id.name == this.#name.text() && id.profile == this.#profile.text());
        this.#name.setError({
            error: !this.#name.text()?.match(CONFIG_FILE_NAME_REGEX(CONFIGURATION_FILE_FORMATS)) || duplicate,
            text: duplicate
                ? "Конфигурация у заданного профиля с таким именем уже существует"
                : "Поддерживаемые форматы - xml, yml, yaml, groovy, properties, json, conf, js, ts"
        })
    };

    #addConfiguration = () => {
        const api = this.#api();
        const request = {
            projectId: this.properties.project.id,
            name: this.#name.text(),
            profile: this.#profile.text(),
            configuration: this.#content.text()
        }
        api.addPreparedConfiguration(request, () => {
            this.#notifications().success("Конфигурация добавлена")
            this.#dialog.close()
        });
    };

    #button = button({
        color: "primary",
        variant: "contained",
        disabled: true,
        label: "Добавить"
    })
    .onClick(this.#addConfiguration)
    .onClick(() => this.#dialog.close());

    #name = text({
        label: "Имя",
        fullWidth: true,
        required: true,
        placeholder: "module-config.yml",
        autoFocus: true,
        regexp: CONFIG_FILE_NAME_REGEX(CONFIGURATION_FILE_FORMATS),
        defaultErrorText: "Поддерживаемые форматы - xml, yml, yaml, groovy, properties, json, conf, js, ts"
    })
    .onTextChanged(name => this.#content.setFileName(name))
    .onTextChanged(this.#validate);

    #profile = text({
        label: "Профиль",
        fullWidth: true,
        required: true,
        placeholder: "development",
        regexp: ENTITY_NAME_REGEX
    })
    .onTextChanged(this.#validate);

    #content = codeEditor({
        label: "Содержимое",
        themeName: platform.themeName() == PlatformTheme.DARK ? CodeEditorTheme.DARK : CodeEditorTheme.LIGHT,
        fileName: this.#name.text()
    })
    .onTextChanged(this.#validate);

    #dialog = dialog({
        label: "Новая конфигурация",
        visible: true,
        fullWidth: true,
        maxWidth: "md",
        disableEnforceFocus: true
    })
    .widget(form(verticalGrid({spacing: 1})
        .pushWidget(this.#name)
        .pushWidget(this.#profile)
        .pushWidget(this.#content)
    ))
    .action(this.#button, {justify: "flex-end"})
    .onClose(() => {
        this.#name.clear()
        this.#profile.clear()
        this.#content.clearText()
    });

    constructor(properties: Properties) {
        super(properties);
    }

    onClose = this.#dialog.onClose;

    draw = this.#dialog.render;
}

export const preparedConfigurationAdditionDialog = (ids: PreparedConfigurationIdentifier[], project: Project) =>
    new PreparedConfigurationAdditionDialog({ids, project});
