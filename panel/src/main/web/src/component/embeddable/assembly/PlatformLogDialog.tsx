import * as React from "react";
import {DispatchWithoutAction} from "react";
import {Widget} from "../../../framework/widgets/Widget";
import {useLogApi} from "../../../api/LogApi";
import {logDialog} from "../log/LogDialog";
import {Closable} from "../../../framework/pattern/Optional";
import {activateLogStream, deactivateLogStream, logStream, onLogUpdated} from "../../../streams/LogStream";

type Properties = {
    logId: number
    loading: boolean
    label: string
}

export class PlatformLogDialog extends Widget<PlatformLogDialog, Properties> implements Closable {
    #api = this.hookValue(useLogApi)

    #dialog = logDialog({
        label: this.properties.label,
        loading: this.properties.loading
    });

    constructor(properties: Properties) {
        super(properties);
        this.onLoad(() => {
            activateLogStream(this.#api().subscribeOnLog(event => logStream.produceEvent(event)))
            this.#api().getLog(properties.logId, log => this.#dialog.setRecords(log.records))
        })
        this.subscribe(() => onLogUpdated(properties.logId, log => this.setRecords(log.records)))
        this.onUnmount(deactivateLogStream)
    }

    setLoading = (loading: boolean) => {
        this.#dialog.setLoading(loading)
        return this;
    }

    setRecords = (records: string[]) => {
        this.#dialog.setRecords(records)
        return this;
    }

    onClose = (action: DispatchWithoutAction) => {
        this.#dialog.onClose(action)
        return this;
    }

    draw = this.#dialog.render;
}

export const platformLogDialog = (properties: Properties) => new PlatformLogDialog(properties)
