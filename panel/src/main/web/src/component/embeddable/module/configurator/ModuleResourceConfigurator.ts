import {Widget} from "../../../../framework/widgets/Widget";
import {Configurator} from "../../../../framework/pattern/Configurator";
import {DispatchWithoutAction} from "react";
import {verticalGrid} from "../../../../framework/dsl/managed/ManagedGrid";
import {label} from "../../../../framework/dsl/managed/ManagedLabel";
import {ResourceIdentifier} from "../../../../model/ResourceTypes";
import {resourceSelector} from "../../common/PlatformSelectors";
import {EXECUTORS} from "../../../../constants/ResourceConstants";
import {ResourcesStore} from "../../../../loader/ResourcesLoader";

type Properties = {
    resources: ResourcesStore
    resourceId?: ResourceIdentifier
}

export class ModuleResourceConfigurator extends Widget<ModuleResourceConfigurator, Properties> implements Configurator<ResourceIdentifier> {
    #resourceIdSelector = resourceSelector({
        ids: this.properties.resources.idsOf(EXECUTORS),
        label: "Запустить в",
        selected: this.properties.resourceId
    });

    #configurator = verticalGrid({spacing: 1, wrap: "nowrap"})
    .pushWidget(label({color: "secondary", variant: "h6", noWrap: true, text: "Ресурс"}))
    .pushWidget(this.#resourceIdSelector);

    onChange = (action: DispatchWithoutAction) => {
        this.#resourceIdSelector.onSelect(action)
        return this;
    }

    configure = () => this.#resourceIdSelector.selected();

    draw = this.#configurator.render;
}

export const moduleResourceConfigurator = (properties: Properties) => new ModuleResourceConfigurator(properties);
