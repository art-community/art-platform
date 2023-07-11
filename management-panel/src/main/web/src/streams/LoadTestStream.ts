import {LoadTest} from "../model/LoadTestingTypes";
import {stream, Stream} from "../framework/pattern/Stream";
import {Dispatch, DispatchWithoutAction} from "react";
import {Notifications} from "../framework/extensions/Notifications";
import {LOAD_TEST_CANCELED_STATE, LOAD_TEST_DONE_STATE, LOAD_TEST_FAILED_STATE, LOAD_TEST_STARTED_ON_RESOURCE_STATE} from "../constants/States";

type LoadTestEvent = LoadTest & { projectName?: string };

export let loadTestStream: Stream<LoadTestEvent>;

export const activateLoadTestStream = (stopAction: DispatchWithoutAction) => loadTestStream = stream<LoadTestEvent>(stopAction);

export const deactivateLoadTestStream = () => loadTestStream?.stop();

export const onLoadTestAdded = (handler: Dispatch<LoadTest>) => {
    const index = loadTestStream.subscribeOnAdd(handler);
    return () => loadTestStream.unsubscribeFromAdd(index);
};
export const onLoadTestDeleted = (handler: Dispatch<LoadTest>) => {
    const index = loadTestStream.subscribeOnDelete(handler);
    return () => loadTestStream.unsubscribeFromDelete(index)
};
export const onLoadTestUpdated = (id: number, handler: Dispatch<LoadTest>) => {
    const index = loadTestStream.subscribeOnUpdateSingle(id, handler);
    return () => loadTestStream.unsubscribeFromUpdateSingle(id, index)
};

export const subscribeOnLoadTest = (notifications: Notifications) => {
    loadTestStream.subscribeOnAdd(loadTest => {
        notifications.success(`Нагрузочный тест ${loadTest.version.version} проекта ${loadTest.projectName} запущен`)
    });
    loadTestStream.subscribeOnDelete(loadTest => {
        notifications.info(`Нагрузочный тест ${loadTest.version.version} удален`)
    });
    loadTestStream.subscribeOnUpdateMany(loadTest => {
        switch (loadTest.state) {
            case LOAD_TEST_FAILED_STATE:
                notifications.error(`Не удалось завершить нагрузочный тест ${loadTest.version.version} проекта ${loadTest.projectName}. Ошибка в журнале теста`);
                break;
            case LOAD_TEST_STARTED_ON_RESOURCE_STATE:
                notifications.info(`Нагрузочный тест ${loadTest.version.version} проекта ${loadTest.projectName} был запущен на ресурсе ${loadTest.resourceId.name}`);
                break;
            case LOAD_TEST_CANCELED_STATE:
                notifications.success(`Нагрузочный тест ${loadTest.version.version} проекта ${loadTest.projectName} был успешно отменен`);
                break;
            case LOAD_TEST_DONE_STATE:
                notifications.success(`Нагрузочный тест ${loadTest.version.version} проекта ${loadTest.projectName} успешно завершен`);
                break;
        }
    });
};
