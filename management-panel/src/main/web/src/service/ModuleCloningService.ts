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

export class ModuleCloningService {
    #fileApi: typeof useFileApi
    #moduleApi: typeof useModuleApi

    constructor(fileApi: typeof useFileApi, moduleApi: typeof useModuleApi) {
        this.#fileApi = fileApi;
        this.#moduleApi = moduleApi;
    }

    #allocateFiles = (baseModule: Module, configuration: ModuleConfigurationDraft, onComplete: Dispatch<ModuleFiles>) => {
        let allocatedFilesCount = 0;

        const additionalFiles: PlatformFileIdentifier[] = [];
        const newAdditionalFiles = configuration.additionalFiles || [];

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

    clone = (projectId: number, baseModule: Module, configuration: ModuleConfigurationDraft) => {
        this.#allocateFiles(baseModule, configuration, files => {
            const request = {
                projectId: baseModule.projectId,
                configuration: {
                    ...configuration,
                    additionalFiles: files.additionalFiles.map(file => file.id)
                }
            };
            this.#moduleApi().startModuleInstallation(request, module => {
                let uploadedFilesCount = 0;

                const newAdditionalFiles = files.additionalFiles.filter(file => Boolean(file.bytes))
                const expectedFilesCount = newAdditionalFiles.length;

                if (isEmptyArray(newAdditionalFiles)) {
                    this.#moduleApi().processModuleInstallation(module);
                    return
                }

                newAdditionalFiles.forEach(newFile => {
                    //Clone existed file
                    if (newFile.bytes!.length <= 0) {
                        const request = {
                            currentFileId: baseModule.additionalFiles!.find(file => file.name == newFile.id.name)!,
                            newFileId: newFile.id
                        }
                        this.#fileApi().cloneFile(request, () => {
                            if (++uploadedFilesCount == expectedFilesCount) {
                                this.#moduleApi().processModuleInstallation(module);
                                return
                            }
                        });
                    }

                    //Upload new file
                    if (newFile.bytes!.length > 0) {
                        const request = {
                            id: newFile.id,
                            bytes: newFile.bytes!
                        };
                        this.#fileApi().uploadFile(request, () => {
                            if (++uploadedFilesCount == expectedFilesCount) {
                                this.#moduleApi().processModuleInstallation(module);
                                return
                            }
                        });
                    }
                });
            })
        })
    }
}

export const moduleCloner = (fileApi: typeof useFileApi, moduleApi: typeof useModuleApi) => new ModuleCloningService(fileApi, moduleApi)
