import {Module} from "../model/ModuleTypes";
import {stream, Stream} from "../framework/pattern/Stream";
import {Dispatch, DispatchWithoutAction} from "react";
import {Notifications} from "../framework/extensions/Notifications";
import {
    MODULE_INSTALLATION_STARTED_STATE, MODULE_INVALID_STATE, MODULE_NOT_INSTALLED_STATE,
    MODULE_RESTART_STARTED_STATE, MODULE_RUN_STATE,
    MODULE_STOP_STARTED_STATE,
    MODULE_STOPPED_STATE,
    MODULE_UNINSTALL_STARTED_STATE,
    MODULE_UPDATE_STARTED_STATE
} from "../constants/States";

type ModuleEvent = Module & { projectName?: string };

export let moduleStream: Stream<ModuleEvent>;

export const activateModuleStream = (stopAction: DispatchWithoutAction) => moduleStream = stream<ModuleEvent>(stopAction);

export const deactivateModuleStream = () => moduleStream?.stop();

export const onModuleAdded = (handler: Dispatch<Module>) => {
    const index = moduleStream.subscribeOnAdd(handler);
    return () => moduleStream.unsubscribeFromAdd(index);
};
export const onAnyModuleUpdated = (handler: Dispatch<Module>) => {
    const index = moduleStream.subscribeOnUpdateMany(handler);
    return () => moduleStream.unsubscribeFromUpdateMany(index);
};
export const onModuleDeleted = (handler: Dispatch<Module>) => {
    const index = moduleStream.subscribeOnDelete(handler);
    return () => moduleStream.unsubscribeFromDelete(index)
};
export const onModuleUpdated = (id: number, handler: Dispatch<Module>) => {
    const index = moduleStream.subscribeOnUpdateSingle(id, handler);
    return () => moduleStream.unsubscribeFromUpdateSingle(id, index)
};

export const subscribeOnModule = (notifications: Notifications) => {
    moduleStream.subscribeOnAdd(module => notifications.success(`Модуль ${module.name} проекта ${module.projectName} устанавливается`));
    moduleStream.subscribeOnDelete(module => notifications.info(`Модуль ${module.name} удален`));
    moduleStream.subscribeOnUpdateMany(module => {
        switch (module.state) {
            case MODULE_INSTALLATION_STARTED_STATE:
                notifications.info(`Модуль ${module.name} проекта ${module.projectName} переустанавливается`);
                return;
            case MODULE_STOP_STARTED_STATE:
                notifications.info(`Модуль ${module.name} проекта ${module.projectName} останавливается`);
                return;
            case MODULE_RESTART_STARTED_STATE:
                notifications.info(`Модуль ${module.name} проекта ${module.projectName} запускается`);
                return;
            case MODULE_UPDATE_STARTED_STATE:
                notifications.info(`Модуль ${module.name} проекта ${module.projectName} обновляется`);
                return;
            case MODULE_UNINSTALL_STARTED_STATE:
                notifications.info(`Модуль ${module.name} проекта ${module.projectName} удаляется`);
                return;

            case MODULE_STOPPED_STATE:
                notifications.warning(`Модуль ${module.name} проекта ${module.projectName} остановлен`);
                return;
            case MODULE_RUN_STATE:
                notifications.success(`Модуль ${module.name} проекта ${module.projectName} активен`);
                return;
            case MODULE_NOT_INSTALLED_STATE:
                notifications.warning(`Модуль ${module.name} проекта ${module.projectName} удален с ресурса`);
                return;
            case MODULE_INVALID_STATE:
                notifications.error(`Не удалось завершить действие над модулем ${module.name} проекта ${module.projectName}`);
                return;
        }
    });
}
