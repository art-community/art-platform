import React, {Dispatch, DispatchWithoutAction} from "react";
import {StringFile} from "../../../../model/PlatformFileTypes";
import {Widget} from "../../../../framework/widgets/Widget";
import {checkBoxPanel} from "../../../../framework/dsl/managed/ManagedPanel";
import {fileCollection} from '../../common/PlatformCollections';
import {platform} from "../../../entry/EntryPoint";
import {PlatformTheme} from "../../../../constants/PlatformTheme";
import {CodeEditorTheme} from "../../../../framework/constants/Constants";
import {MODULE_CONFIGURATION_EDITOR_HEIGHT} from '../../../../constants/ModuleConstants';
import {ManagedTextField} from "../../../../framework/dsl/managed/ManagedTextField";
import {ManagedCodeEditor} from "../../../../framework/dsl/managed/ManagedCodeEditor";
import {isNotEmptyArray} from "../../../../framework/extensions/extensions";
import {CONFIG_FILE_NAME_REGEX} from "../../../../constants/Regexps";
import {event} from "../../../../framework/pattern/Event";
import {Configurable} from "../../../../framework/pattern/Configurable";
import {PreparedConfigurationIdentifier} from "../../../../model/PreparedConfigurationTypes";
import {CONFIGURATION_FILE_FORMATS} from "../../../../constants/ConfigurationConstants";
import {modulePreparedCofigurationAdditionDialog} from "../prepared/ModulePreparedCofigurationAdditionDialog";
import {grid, gridItem, verticalGrid} from "../../../../framework/dsl/managed/ManagedGrid";
import {clickableChip} from "../../../../framework/dsl/simple/SimpleChip";
import {deferredCodeTooltip} from "../../../../framework/dsl/simple/SimpleCodeViewer";
import {usePreparedConfigurationApi} from "../../../../api/PreparedConfigurationApi";
import {label} from "../../../../framework/dsl/managed/ManagedLabel";
import {proxy} from "../../../../framework/widgets/Proxy";
import AddOutlined from "@material-ui/icons/AddOutlined";
import {button} from "../../../../framework/dsl/managed/ManagedButton";
import {optional} from "../../../../framework/pattern/Optional";
import {divider} from "../../../../framework/dsl/simple/SimpleDivider";
import downloadFile from "react-file-download"
import {encoder} from "../../../../constants/EncodingService";
import CancelOutlined from "@material-ui/icons/CancelOutlined";
import {ModuleConfigurator} from "./ModuleConfigurator";

type Properties = {
    manualConfigurations?: StringFile[]
    preparedConfigurations?: PreparedConfigurationIdentifier[]
    projectId: number
    availablePreparedConfigurations: PreparedConfigurationIdentifier[]
    thisConfigurator: ModuleConfigurator
}

class Configuration extends Configurable<Properties> {
    change = event()
}

export class ModuleConfigurationFilesConfigurator extends Widget<ModuleConfigurationFilesConfigurator, Properties, Configuration> {
    #preparedConfigurationApi = this.hookValue(usePreparedConfigurationApi);

    #downloadPreparedConfiguration = (id: PreparedConfigurationIdentifier) => this
    .#preparedConfigurationApi()
    .getPreparedConfiguration(id.id, configuration => downloadFile(encoder().encode(configuration.configuration), configuration.name))

    #receivePreparedConfiguration = (id: PreparedConfigurationIdentifier, setter: Dispatch<string>) => this
    .#preparedConfigurationApi()
    .getPreparedConfiguration(id.id, configuration => setter(configuration.configuration));

    #createPreparedConfiguration = (id: PreparedConfigurationIdentifier) => gridItem(deferredCodeTooltip(clickableChip(id.name,
        () => this.#downloadPreparedConfiguration(id), {
            color: "primary",
            deleteIcon: <CancelOutlined color={"primary"}/>,
            onDelete: () => {
                this.#preparedConfigurations.removeKey(id.id)
                this.configuration.change.execute()
            }
        }),
        setter => this.#receivePreparedConfiguration(id, setter), {
            fileName: id.name,
            themeName: platform.themeName() == PlatformTheme.DARK ? CodeEditorTheme.DARK : CodeEditorTheme.LIGHT
        }),
        id.id
    );

    #validateManualName = (name: ManagedTextField, text: string) => this.properties.thisConfigurator
    .configure().preparedConfigurations?.some(id => id.name == text) && name.setError({
        error: true,
        text: "Имена конфигурационных файлов должны быть уникальны"
    });

    #manualConfigurations = fileCollection({
        addButtonTooltip: "Добавить конфигурацию",
        files: this.properties.manualConfigurations?.map(file => ({name: file.name, content: file.content})),
        editorTheme: platform.themeName() == PlatformTheme.DARK ? CodeEditorTheme.DARK : CodeEditorTheme.LIGHT,
        editorHeight: MODULE_CONFIGURATION_EDITOR_HEIGHT,
        nameDecorator: name => name.onTextChanged(text => this.#validateManualName(name, text)).onTextChanged(this.configuration.change.execute),
        contentDecorator: content => content.onTextChanged(this.configuration.change.execute),
        regexp: CONFIG_FILE_NAME_REGEX(CONFIGURATION_FILE_FORMATS),
        error: "Поддерживаемые форматы - xml, yml, yaml, groovy, properties, json, conf, js, ts",
        direction: "column"
    })
    .onAdd(this.configuration.change.execute)
    .onDelete(this.configuration.change.execute)

    #preparedConfigurations = grid({spacing: 1})
    .pushWidgets(this.properties.preparedConfigurations?.map(this.#createPreparedConfiguration) || [])

    #configurator = checkBoxPanel(verticalGrid({spacing: 1})
    .pushWidget(label({
        text: "Выберите конфигурации",
        color: "secondary",
        variant: "h6",
    }))
    .pushWidget(this.#preparedConfigurations)
    .pushWidget(verticalGrid({alignItems: "center"}).pushWidget(button({
            icon: proxy(<AddOutlined color={"primary"}/>),
            tooltip: "Добавить конфигурацию"
        })
        .onClick(() => this.#preparedDialog.spawn()))
    )
    .pushWidget(divider(1, 1))
    .pushWidget(label({
        text: "Или задайте их",
        color: "secondary",
        variant: "h6"
    }))
    .pushWidget(this.#manualConfigurations), {
        label: "Конфигурационные файлы",
        checked: isNotEmptyArray(this.properties.manualConfigurations) || isNotEmptyArray(this.properties.preparedConfigurations)
    })
    .onCheck(checked => !checked && this.#manualConfigurations.clear() && this.#preparedConfigurations.clear())
    .onCheck(this.configuration.change.execute)

    #preparedDialog = this.add(
        optional(() => {
            const configurationDraft = this.properties.thisConfigurator.configure();
            return modulePreparedCofigurationAdditionDialog({
                ids: this.properties.availablePreparedConfigurations
                .filter(id => !configurationDraft.preparedConfigurations?.some(configuration => configuration.name == id.name))
                .filter(id => !configurationDraft.manualConfigurations?.some(configuration => configuration.name == id.name)),
                projectId: this.properties.projectId
            })
            .onAdd(id => {
                    this.#preparedConfigurations.pushWidget(this.#createPreparedConfiguration(id));
                    this.configuration.change.execute()
                }
            )
        })
    )

    onChange = (action: DispatchWithoutAction) => {
        this.configuration.change.handle(action)
        return this;
    }

    checked = () => this.#configurator.checked();

    configureManual = (): StringFile[] => this.#manualConfigurations.widgets().map(grid => {
        const name = grid.getWidgetByIndex<ManagedTextField>(0).text() || ""
        const content = grid.getWidgetByIndex<ManagedCodeEditor>(1).text() || ""
        return {name, content}
    })

    configurePrepared = (): PreparedConfigurationIdentifier[] => this.#preparedConfigurations
    .keys()
    .map(key => this.properties.availablePreparedConfigurations.find(id => id.id == key))
    .filter(id => id != undefined)
    .map(id => id!)

    draw = this.#configurator.render;
}

export const moduleConfigurationFilesConfigurator = (properties: Properties) =>
    new ModuleConfigurationFilesConfigurator(properties, Configuration)
