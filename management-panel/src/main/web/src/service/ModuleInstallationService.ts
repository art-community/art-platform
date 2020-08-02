import {ModuleConfigurationDraft} from "../model/ModuleTypes";
import {Dispatch} from "react";
import {PlatformFileIdentifier} from "../model/PlatformFileTypes";
import {createModuleName} from "./ModuleService";
import {isEmptyArray} from "../framework/extensions/extensions";
import {useFileApi} from "../api/FileApi";
import {useModuleApi} from "../api/ModuleApi";

type ModuleFiles = { additionalFiles: PlatformFileIdentifier[] };

export class ModuleInstallationService {
    #fileApi: typeof useFileApi
    #moduleApi: typeof useModuleApi

    constructor(fileApi: typeof useFileApi, moduleApi: typeof useModuleApi) {
        this.#fileApi = fileApi;
        this.#moduleApi = moduleApi;
    }

    #allocateFiles = (configuration: ModuleConfigurationDraft, onComplete: Dispatch<ModuleFiles>) => {
        const additionalFiles: PlatformFileIdentifier[] = []

        if (isEmptyArray(configuration.additionalFiles)) {
            onComplete({additionalFiles})
            return
        }

        configuration.additionalFiles?.forEach(file => this.#fileApi().allocateFile(file.name, id => {
            additionalFiles.push(id)
            if (additionalFiles.length == configuration.additionalFiles?.length) {
                onComplete({additionalFiles})
                return
            }
        }));
    }

    install = (projectId: number, configurations: ModuleConfigurationDraft[]) => configurations.forEach(configuration => this.#allocateFiles(configuration, files => {
        const request = {
            projectId,
            name: createModuleName(configuration),
            configuration: {...configuration, ...files}
        };
        this.#moduleApi().startModuleInstallation(request, module => {
            let uploadedFilesCount = 0;

            const expectedFilesCount = configuration.additionalFiles?.length || 0;

            if (isEmptyArray(configuration.additionalFiles)) {
                this.#moduleApi().processModuleInstallation(module);
                return
            }

            configuration.additionalFiles?.forEach((file, index) => this.#fileApi()
            .uploadFile({
                id: files.additionalFiles[index],
                bytes: file.bytes
            }, () => {
                if (++uploadedFilesCount == expectedFilesCount) {
                    this.#moduleApi().processModuleInstallation(module);
                    return
                }
            }));
        })
    }))
}

export const moduleInstaller = (fileApi: typeof useFileApi, moduleApi: typeof useModuleApi) => new ModuleInstallationService(fileApi, moduleApi)
