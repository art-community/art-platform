import {DispatchWithoutAction} from "react";
import {isNotEmptyArray} from "../../../../framework/extensions/extensions";
import {Widget} from "../../../../framework/widgets/Widget";
import {checkBoxPanel} from "../../../../framework/dsl/managed/ManagedPanel";
import {ModuleFile} from "../../../../model/ModuleTypes";
import {Configurator} from "../../../../framework/pattern/Configurator";
import {fileLoader} from "../../../../framework/dsl/managed/ManagedFileLoader";
import {MAX_MODULES_FILE_COUNT, MAX_MODULES_FILE_SIZE_BYTES, MAX_MODULES_FILE_SIZE_MESSAGE} from "../../../../constants/ModuleConstants";

type Properties = {
    defaultFileNames?: string[]
}

export class ModuleAdditionalFilesConfigurator extends Widget<ModuleAdditionalFilesConfigurator, Properties> implements Configurator<ModuleFile[]> {
    #fileLoader = fileLoader({
        defaultFileNames: this.properties.defaultFileNames,
        maxFilesCount: MAX_MODULES_FILE_COUNT,
        maxFileSize: MAX_MODULES_FILE_SIZE_BYTES,
        maxFileSizeAsString: MAX_MODULES_FILE_SIZE_MESSAGE
    });

    #configurator = checkBoxPanel(this.#fileLoader, {
        label: "Дополнительные файлы",
        checked: isNotEmptyArray(this.properties.defaultFileNames)
    })
    .onCheck(checked => !checked && this.#fileLoader.clearFiles())

    onChange = (action: DispatchWithoutAction) => {
        this.#configurator.onCheck(action)
        this.#fileLoader.onFilesChanged(action);
        return this;
    }

    configure = () => this.#fileLoader.files()

    draw = this.#configurator.render;
}

export const moduleAdditionalFilesConfigurator = (files?: string[]) => new ModuleAdditionalFilesConfigurator({defaultFileNames: files})
