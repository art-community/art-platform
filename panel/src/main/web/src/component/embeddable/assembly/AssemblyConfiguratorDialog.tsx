import * as React from "react";
import {Project} from "../../../model/ProjectTypes";
import {Widget} from "../../../framework/widgets/Widget";
import {dialog} from "../../../framework/dsl/managed/ManagedDialog";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {assemblyConfigurator} from "./AssemblyConfigurator";
import {ResourcesStore} from "../../../loader/ResourcesLoader";
import {Closable, optional} from "../../../framework/pattern/Optional";
import {useAssemblyApi} from "../../../api/AssemblyApi";
import {useNotifications} from "../../../framework/hooks/Hooks";

type Properties = {
    project: Project
    resources: ResourcesStore
}

class AssemblyConfiguratorDialog extends Widget<AssemblyConfiguratorDialog, Properties> implements Closable {
    #notifications = this.hookValue(useNotifications);

    #api = this.hookValue(useAssemblyApi);

    #configurator = optional(initialConfiguration => assemblyConfigurator({
        ...this.properties,
        initialConfiguration
    })
    .onValidate(validated => this.#button.setDisabled(!validated)))

    #button = button({
        label: "Сохранить",
        color: "primary",
        variant: "contained",
        disabled: true
    })
    .onClick(() => this.#save());

    #dialog = dialog({
        label: `Настройка сборки проекта ${this.properties.project.name}`,
        visible: true,
        fullWidth: true,
        maxWidth: "lg",
        disableEnforceFocus: true
    })
    .widget(this.#configurator)
    .action(this.#button, {justify: "flex-end"})

    #save = () => this.#api().saveAssemblyConfiguration(this.#configurator.get()!.configure(),
        () => this.#notifications()
        .success("Конфигурация успешно сохранена"),

        error => this.#notifications()
        .customError(() => this.#notifications().createTwoLineSnack("Не удалось сохранить конфигурацию", error.errorMessage)),
    )

    onClose = this.#dialog.onClose;

    constructor(props) {
        super(props);
        this.onLoad(() => this.#api().getAssemblyConfiguration(this.properties.project.id, this.#configurator.spawn));
    }

    draw = this.#dialog.render;
}

export const assemblyConfiguratorDialog = (properties: Properties) => new AssemblyConfiguratorDialog(properties);
