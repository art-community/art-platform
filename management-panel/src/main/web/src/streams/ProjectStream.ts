import {stream, Stream} from "../framework/pattern/Stream";
import {Project} from "../model/ProjectTypes";
import {Dispatch, DispatchWithoutAction} from "react";
import {PROJECT_INITIALIZATION_FAILED_STATE, PROJECT_INITIALIZED_STATE, PROJECT_RELOAD_STARTED_STATE} from "../constants/States";
import {Notifications} from "../framework/extensions/Notifications";

export let projectStream: Stream<Project>;

export const activateProjectStream = (stopAction: DispatchWithoutAction) => projectStream = stream<Project>(stopAction);

export const deactivateProjectStream = () => projectStream?.stop();

export const onProjectAdded = (handler: Dispatch<Project>) => {
    const index = projectStream.subscribeOnAdd(handler);
    return () => projectStream.unsubscribeFromAdd(index);
};

export const onProjectDeleted = (handler: Dispatch<Project>) => {
    const index = projectStream.subscribeOnDelete(handler);
    return () => projectStream.unsubscribeFromDelete(index)
};

export const onProjectUpdated = (id: number, handler: Dispatch<Project>) => {
    const index = projectStream.subscribeOnUpdateSingle(id, handler);
    return () => projectStream.unsubscribeFromUpdateSingle(id, index)
};

export const subscribeOnProjects = (notifications: Notifications) => {
    projectStream.subscribeOnAdd(project => {
        notifications.success(`Проект ${project.name} добавлен`)
    });
    projectStream.subscribeOnDelete(project => {
        notifications.info(`Проект ${project.name} удален`)
    });
    projectStream.subscribeOnUpdateMany(project => {
        switch (project.state) {
            case PROJECT_INITIALIZED_STATE:
                notifications.success(`Проект ${project.name} успешно инициализирован`);
                return;
            case PROJECT_INITIALIZATION_FAILED_STATE:
                notifications.error(`Не удалось инициализировать проект ${project.name}`);
                return;
            case PROJECT_RELOAD_STARTED_STATE:
                notifications.info(`Проект ${project.name} обновляется`);
                return;
        }
    });


}

