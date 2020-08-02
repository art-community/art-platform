import {stream, Stream} from "../framework/pattern/Stream";
import {Log} from "../model/LogTypes";
import {Dispatch, DispatchWithoutAction} from "react";

export let logStream: Stream<Log>;

export const activateLogStream = (stopAction: DispatchWithoutAction) => logStream = stream<Log>(stopAction);

export const deactivateLogStream = () => logStream?.stop();

export const onLogUpdated = (id: number, handler: Dispatch<Log>) => {
    const index = logStream.subscribeOnUpdateSingle(id, handler);
    return () => logStream.unsubscribeFromUpdateSingle(id, index)
};
