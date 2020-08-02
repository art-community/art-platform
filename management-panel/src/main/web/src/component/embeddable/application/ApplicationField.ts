import {DispatchWithoutAction} from "react";
import {Widget} from "../../../framework/widgets/Widget";

export interface ApplicationField<T> {
    clear(): void

    error(): boolean

    value(): T

    setValue(value: T): ApplicationField<T>

    onChange(action: DispatchWithoutAction): ApplicationField<T>

    widget: Widget<any>
}
