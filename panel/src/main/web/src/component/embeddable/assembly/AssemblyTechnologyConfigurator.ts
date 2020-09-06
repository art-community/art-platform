import {radioPanel} from "../../../framework/dsl/managed/ManagedPanel";
import {verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {GRADLE} from "../../../constants/TechnologyConstants";
import {gradleAssemblyConfigurator} from "../gradle/GradleAssemblyConfigurator";
import {GradleAssemblyConfiguration} from "../../../model/GradleTypes";
import {ResourcesStore} from "../../../loader/ResourcesLoader";
import {DispatchWithoutAction} from "react";
import {group} from "../../../framework/dsl/simple/SimpleGroup";
import {lazy} from "../../../framework/pattern/Lazy";
import {Configurable} from "../../../framework/pattern/Configurable";
import {event} from "../../../framework/pattern/Event";
import {Widget} from "../../../framework/widgets/Widget";

type Properties = {
    gradleInitialConfiguration?: GradleAssemblyConfiguration
    resources: ResourcesStore
}

class Configuration extends Configurable<Properties> {
    change = event();
}

export class AssemblyTechnologyConfigurator extends Widget<AssemblyTechnologyConfigurator, Properties, Configuration> {
    #gradleConfigurator = lazy(() => gradleAssemblyConfigurator({
        configuration: this.properties.gradleInitialConfiguration,
        configureCache: true,
        configureArguments: true,
        resources: this.properties.resources
    })
    .onChange(this.configuration.change.execute));

    #gradleBuilder = lazy(() => radioPanel(verticalGrid({spacing: 1, wrap: "nowrap"})
    .pushWidget(this.#gradleConfigurator()), {
        checked: Boolean(this.properties.gradleInitialConfiguration),
        label: "Gradle"
    })
    .onCheck(this.configuration.change.execute))

    gradleAssemblyConfiguration = () => this.technology() == GRADLE
        ? this.#gradleConfigurator().configure()
        : undefined;

    onChange = (action: DispatchWithoutAction) => {
        this.configuration.change.handle(action)
        return this;
    }

    technology = () => {
        if (this.#gradleBuilder().checked()) {
            return GRADLE;
        }
        return undefined;
    }

    hasTechnology = () => this.#gradleBuilder().checked();

    draw = () => group().widget(this.#gradleBuilder()).render()
}

export const assemblyTechnologyConfigurator = (properties: Properties) => new AssemblyTechnologyConfigurator(properties, Configuration)
