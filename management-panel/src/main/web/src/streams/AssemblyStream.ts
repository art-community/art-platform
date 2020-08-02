import {Assembly} from "../model/AssemblyTypes";
import {stream, Stream} from "../framework/pattern/Stream";
import {Dispatch, DispatchWithoutAction} from "react";
import {ASSEMBLY_CANCELED_STATE, ASSEMBLY_DONE_STATE, ASSEMBLY_FAILED_STATE, ASSEMBLY_RESTARTED_STATE, ASSEMBLY_STARTED_ON_RESOURCE_STATE} from "../constants/States";
import {Notifications} from "../framework/extensions/Notifications";

type AssemblyEvent = Assembly & { projectName?: string };

export let assemblyStream: Stream<AssemblyEvent>;

export const activateAssemblyStream = (stopAction: DispatchWithoutAction) => assemblyStream = stream<AssemblyEvent>(stopAction);

export const deactivateAssemblyStream = () => assemblyStream?.stop();

export const onAssemblyAdded = (handler: Dispatch<Assembly>) => {
    const index = assemblyStream.subscribeOnAdd(handler);
    return () => assemblyStream.unsubscribeFromAdd(index);
};
export const onAssemblyDeleted = (handler: Dispatch<Assembly>) => {
    const index = assemblyStream.subscribeOnDelete(handler);
    return () => assemblyStream.unsubscribeFromDelete(index)
};
export const onAnyAssemblyUpdated = (handler: Dispatch<Assembly>) => {
    const index = assemblyStream.subscribeOnUpdateMany(handler);
    return () => assemblyStream.unsubscribeFromUpdateMany(index);
};
export const onAssemblyUpdated = (id: number, handler: Dispatch<Assembly>) => {
    const index = assemblyStream.subscribeOnUpdateSingle(id, handler);
    return () => assemblyStream.unsubscribeFromUpdateSingle(id, index)
};

export const subscribeOnAssembly = (notifications: Notifications) => {
    assemblyStream.subscribeOnAdd(assembly => {
        notifications.success(`Сборка ${assembly.version.version} проекта ${assembly.projectName} запущена`)
    });
    assemblyStream.subscribeOnDelete(assembly => {
        notifications.info(`Сборка ${assembly.version.version} удалена`)
    });
    assemblyStream.subscribeOnUpdateMany(assembly => {
        switch (assembly.state) {
            case ASSEMBLY_FAILED_STATE:
                notifications.error(`Не удалось завершить сборку ${assembly.version.version} проекта ${assembly.projectName}. Ошибка в журнале сборки`);
                break;
            case ASSEMBLY_RESTARTED_STATE:
                notifications.success(`Сборка ${assembly.version.version} проекта ${assembly.projectName} была перезапущена`);
                break;
            case ASSEMBLY_STARTED_ON_RESOURCE_STATE:
                notifications.info(`Сборка ${assembly.version.version} проекта ${assembly.projectName} была запущена на ресурсе ${assembly.resourceId.name}`);
                break;
            case ASSEMBLY_CANCELED_STATE:
                notifications.success(`Сборка ${assembly.version.version} проекта ${assembly.projectName} была успешно отменена`);
                break;
            case ASSEMBLY_DONE_STATE:
                notifications.success(`Сборка ${assembly.version.version} проекта ${assembly.projectName} успешно завершена`);
                break;
        }
    });

};
