import React, {DispatchWithoutAction} from "react";
import {grid, horizontalGrid, verticalGrid} from "../../../../framework/dsl/managed/ManagedGrid";
import {Widget} from "../../../../framework/widgets/Widget";
import {button} from "../../../../framework/dsl/managed/ManagedButton";
import {proxy} from "../../../../framework/widgets/Proxy";
import AddOutlined from "@material-ui/icons/AddOutlined";
import {moduleFilebeatConfigurator} from "./filebeat/ModuleFilebeatConfigurator";
import {ResourceIdentifier} from "../../../../model/ResourceTypes";
import {event} from "../../../../framework/pattern/Event";
import {Configurable} from "../../../../framework/pattern/Configurable";
import {moduleApplicationAdditionDialog} from "./ModuleApplicationAdditionDialog";
import {ModuleApplication} from "../../../../model/ModuleTypes";
import {asynchronous} from "../../../../framework/extensions/extensions";
import {ApplicationsStore} from "../../../../loader/ApplicationsLoader";
import {label} from "../../../../framework/dsl/managed/ManagedLabel";
import {divider} from "../../../../framework/dsl/simple/SimpleDivider";
import {Configurator} from "../../../../framework/pattern/Configurator";
import {moduleApplicationCard, ModuleApplicationCard} from "./ModuleApplicationCard";
import {ModuleConfigurator} from "../configurator/ModuleConfigurator";
import {FILEBEAT_APPLICATION} from "../../../../constants/ApplicationConstants";
import {FilebeatModuleApplication} from "../../../../model/FilebeatTypes";
import {optional} from "../../../../framework/pattern/Optional";


export type ModuleApplicationConfigurator<T> = Configurator<T> & {
    isValid: () => boolean

    onChange: (action: DispatchWithoutAction) => ModuleApplicationConfigurator<T>
}

type Properties = {
    resourcesIds: ResourceIdentifier[]
    applications: ApplicationsStore
    moduleApplications?: ModuleApplication[]
    thisConfigurator: ModuleConfigurator
}

class Configuration extends Configurable<Properties> {
    change = event()
}

export class ModuleApplicationsCollection extends Widget<ModuleApplicationsCollection, Properties, Configuration> implements Configurator<ModuleApplication[]> {
    #additionDialog = this.add(optional(() => moduleApplicationAdditionDialog({
            applicationIds: this.properties.applications.ids.filter(id => !this.#cards.keys().some(type => id.type == type)),
            filebeatFactory: id => moduleFilebeatConfigurator({
                moduleApplication: this.#cards
                .getWidgetByKey<ModuleApplicationCard>(FILEBEAT_APPLICATION)
                ?.moduleApplication()?.application as FilebeatModuleApplication,

                resourceIds: this.properties.resourcesIds,
                application: this.properties.applications.filebeatOf(id),
                thisModuleConfigurator: this.properties.thisConfigurator
            })
        })
        .onAdd(moduleApplication => this.#addCard(moduleApplication))
        .onAdd(this.configuration.change.execute))
    )

    #cards = grid({spacing: 1})

    #collection = verticalGrid({spacing: 1, wrap: "nowrap"})
    .pushWidget(label({variant: "h6", text: "Приложения", color: "secondary"}))
    .pushWidget(divider(1, 1))
    .pushWidget(horizontalGrid({spacing: 1, wrap: "nowrap", alignItems: "flex-end"})
        .pushWidget(this.#cards)
        .pushWidget(button({
            icon: proxy(<AddOutlined color={"secondary"}/>),
            tooltip: "Добавить приложение"
        })
        .onClick(this.#additionDialog.spawn))
    );

    #addCard = (moduleApplication: ModuleApplication) => this.#cards.pushWidget(moduleApplicationCard({
        application: moduleApplication,
        filebeatFactory: () => moduleFilebeatConfigurator({
            moduleApplication: this.#cards
            .getWidgetByKey<ModuleApplicationCard>(FILEBEAT_APPLICATION)
            ?.moduleApplication()?.application as FilebeatModuleApplication,

            resourceIds: this.properties.resourcesIds,
            application: this.properties.applications.filebeatOf(moduleApplication.applicationId),
            thisModuleConfigurator: this.properties.thisConfigurator
        })
    })
    .onDelete(this.configuration.change.execute)
    .onEdit(this.configuration.change.execute)
    .onDelete(() => this.#cards.removeKey(moduleApplication.applicationId.type)))

    onChange = (action: DispatchWithoutAction) => {
        this.configuration.change.handle(action)
        return this;
    }

    configure = () => this.#cards.widgets().map((card: ModuleApplicationCard) => card.moduleApplication())

    constructor(properties: Properties) {
        super(properties, Configuration);
        asynchronous(() => this.properties.moduleApplications?.forEach(this.#addCard));
    }

    draw = this.#collection.render;
}

export const moduleApplicationsCollection = (properties: Properties) => new ModuleApplicationsCollection(properties)
