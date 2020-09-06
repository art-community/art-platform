import * as React from "react";
import {Dispatch} from "react";
import {useTheme} from "@material-ui/core";
import {EXECUTORS, REGISTRIES} from "../../../constants/ResourceConstants";
import {observe} from "../../../framework/pattern/Observable";
import {Widget} from "../../../framework/widgets/Widget";
import {ResourcesStore} from "../../../loader/ResourcesLoader";
import {resourceSelector} from "../common/PlatformSelectors";
import {AssemblyConfiguration} from "../../../model/AssemblyTypes";
import {Project} from "../../../model/ProjectTypes";
import {group} from "../../../framework/dsl/simple/SimpleGroup";
import {styled} from "../../../framework/widgets/Styled";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {hooked} from "../../../framework/pattern/Hooked";
import {conditional} from "../../../framework/pattern/Conditional";
import {Configurable} from "../../../framework/pattern/Configurable";
import {event} from "../../../framework/pattern/Event";
import {validateAssemblyConfiguration} from "../../../validator/AssemblyConfigurationValidator";
import {Configurator} from "../../../framework/pattern/Configurator";
import {assemblyTechnologyConfigurator} from "./AssemblyTechnologyConfigurator";
import {divider} from "../../../framework/dsl/simple/SimpleDivider";
import {lazy} from "../../../framework/pattern/Lazy";
import {assemblyArtifactsConfigurator} from "../artifact/AssemblyArtifactsConfigurator";

type Properties = {
    project: Project
    resources: ResourcesStore
    initialConfiguration: AssemblyConfiguration
}

const useStyle = () => {
    const theme = useTheme();
    return observe(theme).render(() => ({
        configurator: {
            marginTop: theme.spacing(2),
            marginBottom: theme.spacing(2)
        },
        artifacts: {
            marginTop: theme.spacing(2),
            marginBottom: theme.spacing(2)
        },
        builder: {
            marginTop: theme.spacing(2)
        }
    }));
};

class Configuration extends Configurable<Properties> {
    validate = event<boolean>()
}

export class AssemblyConfigurator extends Widget<AssemblyConfigurator, Properties, Configuration> implements Configurator<AssemblyConfiguration> {
    #resourceSelector = resourceSelector({
        ids: this.properties.resources.idsOf(EXECUTORS),
        selected: this.properties.initialConfiguration.defaultResourceId,
        label: "Ресурс для сборки",
    });

    #projectArtifacts = () => this.properties.project.artifacts
    .filter(artifact => artifact)
    .unique(artifact => artifact.name);

    #technologyConfigurator = assemblyTechnologyConfigurator({
        gradleInitialConfiguration: this.properties.initialConfiguration.gradleConfiguration,
        resources: this.properties.resources
    })
    .onChange(() => {
        this.#validate()
        this.#assemblyArtifacts.notify()
    })

    #assemblyArtifactsConfigurator = lazy(() => assemblyArtifactsConfigurator({
        assemblyTechnologyConfigurator: this.#technologyConfigurator,
        projectArtifacts: this.#projectArtifacts(),
        resourceIds: this.properties.resources.idsOf(REGISTRIES),
        assemblyTechnology: this.#technologyConfigurator.technology()!,
        initialConfigurations: this.properties.initialConfiguration.artifactConfigurations,
    })
    .onChange(() => this.#validate()));

    #assemblyArtifacts = conditional(this.#technologyConfigurator.hasTechnology)
    .persist(() => group()
    .widget(divider())
    .widget(label({color: "primary", variant: "h6", text: "Артефакты"}))
    .widget(this.#assemblyArtifactsConfigurator()));

    #validate = () => this.configuration.validate.execute(validateAssemblyConfiguration(this.configure()))

    #configurator = group()
    .widget(this.#resourceSelector)
    .widget(divider())
    .widget(hooked(useStyle).cache(style => styled(group()
        .widget(label({color: "primary", variant: "h6", text: "Сборщик"}))
        .widget(this.#technologyConfigurator), style.builder))
    )
    .widget(this.#assemblyArtifacts);

    #styledConfigurator = hooked(useStyle).cache(style => styled(this.#configurator, style.configurator));

    useValidate = this.extract(configuration => configuration.validate)

    onValidate = (action: Dispatch<boolean>) => {
        this.useValidate(validated => validated.handle(action))
        return this;
    }

    configure = () => ({
        id: this.properties.initialConfiguration.id,
        defaultResourceId: this.#resourceSelector.selected(),
        artifactConfigurations: this.#assemblyArtifactsConfigurator().configure(),
        gradleConfiguration: this.#technologyConfigurator.gradleAssemblyConfiguration(),
        technology: this.#technologyConfigurator.technology()
    });

    draw = this.#styledConfigurator.render;
}

export const assemblyConfigurator = (properties: Properties) => new AssemblyConfigurator(properties, Configuration);
