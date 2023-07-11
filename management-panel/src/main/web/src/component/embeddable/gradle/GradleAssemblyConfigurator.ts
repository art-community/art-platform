import {gradleInitScript} from "./GradleInitialScript";
import {Widget} from "../../../framework/widgets/Widget";
import {verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {gradleVersionSelector, jdkVersionSelector} from "../common/PlatformSelectors";
import {GradleAssemblyConfiguration} from "../../../model/GradleTypes";
import {text} from "../../../framework/dsl/managed/ManagedTextField";
import {checkBoxPanel} from "../../../framework/dsl/managed/ManagedPanel";
import {propertiesCollection} from "../common/PlatformCollections";
import {ResourcesStore} from "../../../loader/ResourcesLoader";
import {lazy} from "../../../framework/pattern/Lazy";
import {GRADLE_KTS_SCRIPT_FORMAT} from "../../../constants/GradleConstants";
import {Configurator} from "../../../framework/pattern/Configurator";
import {Configurable} from "../../../framework/pattern/Configurable";
import {event} from "../../../framework/pattern/Event";
import {DispatchWithoutAction} from "react";
import {isNotEmptyArray} from "../../../framework/extensions/extensions";

type Properties = {
    configureCache?: boolean
    configureArguments?: boolean
    resources: ResourcesStore
    configuration?: GradleAssemblyConfiguration;
}

class Configuration extends Configurable<Properties> {
    change = event();
}

class GradleAssemblyConfigurator extends Widget<GradleAssemblyConfigurator, Properties, Configuration> implements Configurator<GradleAssemblyConfiguration> {
    #versionSelector = gradleVersionSelector({selected: this.properties.configuration?.version})
    .onSelect(this.configuration.change.execute)

    #jdkVersionSelector = jdkVersionSelector({selected: this.properties.configuration?.jdkVersion})
    .onSelect(this.configuration.change.execute)

    #arguments = text({
        fullWidth: true,
        placeholder: ":clean",
        label: "Аргументы",
        value: this.properties.configuration?.arguments
    })
    .onTextChanged(this.configuration.change.execute);

    #cache = text({
        fullWidth: true,
        placeholder: "gradleCacheServer",
        label: "Свойство под URL кэш сервера",
        value: this.properties.configuration?.cacheConfiguration?.serverUrlProperty
    })
    .onTextChanged(this.configuration.change.execute);

    #initScript = gradleInitScript({
        format: this.properties.configuration?.initScriptFormat || GRADLE_KTS_SCRIPT_FORMAT,
        kotlinContent: this.properties.configuration?.initScriptKotlinContent,
        groovyContent: this.properties.configuration?.initScriptGroovyContent
    })
    .onChange(this.configuration.change.execute);


    #propertyCollection = lazy(() => propertiesCollection({
        properties: this.properties.configuration?.properties || [],
        resources: this.properties.resources,
        decorator: property => property.onPropertyChange(this.configuration.change.execute)
    })
    .onAdd(this.configuration.change.execute)
    .onDelete(this.configuration.change.execute));

    #configurator = lazy(() => {
        const grid = verticalGrid({spacing: 1, wrap: "nowrap"})
        .pushWidget(this.#jdkVersionSelector)
        .pushWidget(this.#versionSelector)

        if (this.properties?.configureArguments) {
            grid.pushWidget(checkBoxPanel(this.#arguments, {
                    label: "Аргументы запуска",
                    wrapLabel: false,
                    checked: Boolean(this.properties.configuration?.arguments)
                })
                .onCheck(checked => !checked && this.#arguments.clear())
            )
        }

        if (this.properties?.configureCache) {
            grid.pushWidget(checkBoxPanel(this.#cache, {
                    label: "Параметры кэширования",
                    wrapLabel: false,
                    checked: Boolean(this.properties.configuration?.cacheConfiguration?.serverUrlProperty)
                })
                .onCheck(checked => !checked && this.#cache.clear())
            )
        }

        const collection = this.#propertyCollection();
        return grid
        .pushWidget(this.#initScript)
        .pushWidget(checkBoxPanel(collection, {
                label: "Свойства",
                wrapLabel: false,
                checked: isNotEmptyArray(this.properties.configuration?.properties)
            })
            .onCheck(checked => !checked && collection.clear())
        );
    })

    onChange = (action: DispatchWithoutAction) => {
        this.configuration.change.handle(action);
        return this;
    }

    configure = () => ({
        arguments: this.#arguments.text(),
        version: this.#versionSelector.selected(),
        jdkVersion: this.#jdkVersionSelector.selected(),
        initScriptGroovyContent: this.#initScript.groovyContent(),
        initScriptKotlinContent: this.#initScript.kotlinContent(),
        initScriptFormat: this.#initScript.format(),
        properties: this.#propertyCollection().widgets().map(property => property.getProperty()),
        cacheConfiguration: this.#cache.text() ? {serverUrlProperty: this.#cache.text()} : undefined
    })

    draw = () => this.#configurator().render();
}

export const gradleAssemblyConfigurator = (properties: Properties) => new GradleAssemblyConfigurator(properties, Configuration);
