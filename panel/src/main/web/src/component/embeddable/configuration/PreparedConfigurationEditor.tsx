import {PreparedConfigurationIdentifier} from "../../../model/PreparedConfigurationTypes";
import {Widget} from "../../../framework/widgets/Widget";
import {codeEditor} from "../../../framework/dsl/managed/ManagedCodeEditor";
import {platform} from "../../entry/EntryPoint";
import {PlatformTheme} from "../../../constants/PlatformTheme";
import {CodeEditorTheme, DEFAULT_CODE_EDITOR_HEIGHT, handleEnter} from "../../../framework/constants/Constants";
import {usePreparedConfigurationApi} from "../../../api/PreparedConfigurationApi";
import {grid, horizontalGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {proxy} from "../../../framework/widgets/Proxy";
import DeleteOutlined from "@material-ui/icons/DeleteOutlined";
import SaveOutlined from "@material-ui/icons/SaveOutlined";
import * as React from "react";
import {DispatchWithoutAction} from "react";
import {Configurable} from "../../../framework/pattern/Configurable";
import {event} from "../../../framework/pattern/Event";
import {panel} from "../../../framework/dsl/managed/ManagedPanel";
import {useNotifications} from "../../../framework/hooks/Hooks";
import {ModulesLoader} from "../../../loader/ModulesLoader";
import {isEmptyArray, isNotEmptyArray} from "../../../framework/extensions/extensions";
import {labelChip} from "../../../framework/dsl/simple/SimpleChip";
import {useModuleApi} from "../../../api/ModuleApi";
import {dialog} from "../../../framework/dsl/managed/ManagedDialog";
import {optional} from "../../../framework/pattern/Optional";
import {MODULE_RUN_STATE} from "../../../constants/States";


type Properties = {
    id: PreparedConfigurationIdentifier
    modules: ModulesLoader
}

class Configuration extends Configurable<Properties> {
    delete = event()
}

class PreparedConfigurationEditor extends Widget<PreparedConfigurationEditor, Properties, Configuration> {
    #configurationApi = this.hookValue(usePreparedConfigurationApi);

    #moduleApi = this.hookValue(useModuleApi);

    #notifications = this.hookValue(useNotifications);

    #usedByModules = () => this.properties.modules
    .get
    .filter(module => module.preparedConfigurations?.some(configuration => configuration.id == this.properties.id.id));

    #editor = codeEditor({
        themeName: platform.themeName() == PlatformTheme.DARK ? CodeEditorTheme.DARK : CodeEditorTheme.LIGHT,
        height: DEFAULT_CODE_EDITOR_HEIGHT,
        receiver: setter => this.#configurationApi().getPreparedConfiguration(this.properties.id.id, configuration => setter(configuration.configuration)),
        fileName: this.properties.id.name
    })
    .onTextChanged(text => this.#saveButton.setDisabled(!Boolean(text)))

    #deleteButton = button({
        icon: proxy(<DeleteOutlined/>),
        color: "primary",
        tooltip: isEmptyArray(this.#usedByModules()) ? "Удалить" : "Для начала удалите конфигурацию из модулей",
        disabled: isNotEmptyArray(this.#usedByModules())
    })
    .onClick(() => this.#configurationApi().deletePreparedConfiguration(this.properties.id.id, () => {
        this.#notifications().success("Конфигурация удалена")
        this.configuration.delete.execute()
    }))

    #saveButton = button({
        icon: proxy(<SaveOutlined/>),
        color: "primary",
        tooltip: "Сохранить",
        disabled: !Boolean(this.#editor.text())
    })
    .onClick(() => this.#configurationApi().updatePreparedConfiguration({
        id: this.properties.id.id,
        name: this.properties.id.name,
        profile: this.properties.id.profile,
        projectId: this.properties.id.projectId,
        configuration: this.#editor.text()
    }, () => {
        this.#notifications().success("Конфигурация обновлена")
        if (isNotEmptyArray(this.#usedByModules().filter(module => module.state == MODULE_RUN_STATE))) {
            this.#saveDialog.spawn()
        }
    }))

    #usedByModuleChips = grid({spacing: 1})
    .pushWidgets(this.#usedByModules()
    .map(module => labelChip(module.name, {color: "primary"})));

    #editorPanel = panel(verticalGrid({spacing: 1}).pushWidget(this.#editor).pushWidget(this.#usedByModuleChips), {
        label: `Конфигурация ${this.properties.id.name}`,
        summaryRightWidget: horizontalGrid({spacing: 1})
        .pushWidget(this.#deleteButton)
        .pushWidget(this.#saveButton)
    });

    #updateModulesButton = button({
        label: "Да, жги!",
        variant: "contained",
        color: "primary",
        fullWidth: true
    })
    .onClick(() => {
        this.#saveDialog.destroy()
        this.#usedByModules()
        .filter(module => module.state == MODULE_RUN_STATE)
        .forEach(module => this.#moduleApi().refreshModuleArtifact(module.id))
    });

    #saveDialog = this.add(optional(() => dialog({
            visible: true,
            label: "Хотите обновить модули ?",
            onKeyDown: handleEnter(this.#updateModulesButton.click)
        })
        .widget(grid({spacing: 1, justify: "center"})
            .pushWidgets(this.#usedByModules()
            .filter(module => module.state == MODULE_RUN_STATE)
            .map(module => labelChip(module.name, {color: "primary"})))
        )
        .actions([this.#updateModulesButton,
            button({
                label: "Нет, потом займусь",
                variant: "outlined",
                color: "secondary",
                fullWidth: true
            })
            .onClick(() => this.#saveDialog.destroy())
        ], {spacing: 1, justify: "flex-end"})
    ))

    onDelete = (action: DispatchWithoutAction) => {
        this.configuration.delete.handle(action)
        return this;
    }

    constructor(properties: Properties) {
        super(properties, Configuration);
        platform.onThemeNameChanged(theme => this.#editor.setThemeName(theme == PlatformTheme.DARK ? CodeEditorTheme.DARK : CodeEditorTheme.LIGHT))
    }

    key = () => this.properties.id.id;

    draw = this.#editorPanel.render;
}

export const preparedConfigurationEditor = (properties: Properties) => new PreparedConfigurationEditor(properties);
