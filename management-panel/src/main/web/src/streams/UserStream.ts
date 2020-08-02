import {stream, Stream} from "../framework/pattern/Stream";
import {User} from "../model/UserTypes";
import {Dispatch, DispatchWithoutAction} from "react";
import {Notifications} from "../framework/extensions/Notifications";

export let userStream: Stream<User>;

export const activateUserStream = (stopAction: DispatchWithoutAction) => userStream = stream<User>(stopAction);

export const deactivateUserStream = () => userStream?.stop();

export const onUserAdded = (handler: Dispatch<User>) => {
    const index = userStream.subscribeOnAdd(handler);
    return () => userStream.unsubscribeFromAdd(index);
};

export const onUserDeleted = (handler: Dispatch<User>) => {
    const index = userStream.subscribeOnDelete(handler);
    return () => userStream.unsubscribeFromDelete(index)
};

export const onUserUpdated = (id: number, handler: Dispatch<User>) => {
    const index = userStream.subscribeOnUpdateSingle(id, handler);
    return () => userStream.unsubscribeFromUpdateSingle(id, index)
};

export const subscribeOnUsers = (notifications: Notifications) => {
    userStream.subscribeOnAdd(user => notifications.success(`Пользователь ${user.name} добавлен`));
    userStream.subscribeOnDelete(user => notifications.info(`Пользователь ${user.name} удален`));
}

