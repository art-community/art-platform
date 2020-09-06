import {Module, ModuleConfigurationDraft} from "../model/ModuleTypes";
import {Dispatch} from "react";
import {PlatformFileIdentifier} from "../model/PlatformFileTypes";
import {isEmptyArray} from "../framework/extensions/extensions";
import {useFileApi} from "../api/FileApi";
import {useModuleApi} from "../api/ModuleApi";

type AllocatedModuleFile = {
    id: PlatformFileIdentifier
    bytes?: Buffer
}

type ModuleFiles = {
    additionalFiles: AllocatedModuleFile[]
};

export class ModuleUpdatingService {
    #fileApi: typeof useFileApi
    #moduleApi: typeof useModuleApi

    constructor(fileApi: typeof useFileApi, moduleApi: typeof useModuleApi) {
        this.#fileApi = fileApi;
        this.#moduleApi = moduleApi;
    }

    #allocateFiles = (baseModule: Module, configuration: ModuleConfigurationDraft, onComplete: Dispatch<ModuleFiles>) => {
        let allocatedFilesCount = 0;

        const additionalFiles: PlatformFileIdentifier[] = baseModule.additionalFiles
        ?.filter(file => configuration.additionalFiles?.some(newFile => newFile.name == file.name)) || [];

        const newAdditionalFiles = configuration.additionalFiles
        ?.filter(file => file.bytes.length != 0 && !baseModule.additionalFiles?.some(current => current.name == file.name)) || [];


        if (isEmptyArray(newAdditionalFiles)) {
            onComplete({
                additionalFiles: additionalFiles.map(file => ({
                    id: file,
                    bytes: configuration.additionalFiles?.find(newFile => newFile.name == file.name)?.bytes
                }))
            })
            return;
        }
        const newFilesCount = newAdditionalFiles?.length || 0;

        const complete = () => onComplete({
            additionalFiles: additionalFiles.map(file => ({
                id: file,
                bytes: configuration.additionalFiles?.find(newFile => newFile.name == file.name)?.bytes
            }))
        });

        newAdditionalFiles.forEach(file => this.#fileApi().allocateFile(file.name, id => {
            additionalFiles.push(id);
            if (++allocatedFilesCount == newFilesCount) {
                complete();
                return
            }
        }))
    }

    update = (projectId: number, baseModule: Module, configuration: ModuleConfigurationDraft) => {
        this.#allocateFiles(baseModule, configuration, files => {
            const request = {
                moduleId: baseModule.id,
                newModuleConfiguration: {
                    ...configuration,
                    additionalFiles: files.additionalFiles.map(file => file.id)
                }
            };
            this.#moduleApi().startModuleUpdating(request, module => {
                let uploadedFilesCount = 0;

                const newAdditionalFiles = files.additionalFiles.filter(file => file.bytes && file.bytes.length > 0)
                const expectedFilesCount = newAdditionalFiles.length;

                if (isEmptyArray(newAdditionalFiles)) {
                    this.#moduleApi().processModuleUpdating(module);
                    return
                }

                newAdditionalFiles.forEach(newFile => {
                    const request = {
                        id: newFile.id,
                        bytes: newFile.bytes!
                    };
                    this.#fileApi().uploadFile(request, () => {
                        if (++uploadedFilesCount == expectedFilesCount) {
                            this.#moduleApi().processModuleUpdating(module);
                            return
                        }
                    });
                });
            })
        })
    }
}

export const moduleUpdater = (fileApi: typeof useFileApi, moduleApi: typeof useModuleApi) => new ModuleUpdatingService(fileApi, moduleApi)
