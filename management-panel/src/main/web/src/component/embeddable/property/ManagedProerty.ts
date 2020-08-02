import {Dispatch} from "react";
import {horizontalGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {text} from "../../../framework/dsl/managed/ManagedTextField";
import {radio} from "../../../framework/dsl/managed/ManagedRadio";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {Property} from "../../../model/Property";
import {resourceProperty} from "./ManagedResourceProperty";
import {Configurable} from "../../../framework/pattern/Configurable";
import {TextProperty} from "../../../model/TextProperty";
import {ResourceProperty} from "../../../model/ResourceProperty";
import {Widget} from "../../../framework/widgets/Widget";
import {ResourcesStore} from "../../../loader/ResourcesLoader";
import {RESOURCE_PROPERTY, TEXT_PROPERTY, VERSION_PROPERTY} from "../../../constants/PropertySources";

type Properties = {
    property?: Property
    resources: ResourcesStore
}

class Configuration extends Configurable<Properties> {
    currentProperty = this.property(this.defaultProperties.property || {type: TEXT_PROPERTY} as Property);

    disabled = this.property(false);

    duplicate = this.property(false);
}

export class ManagedProperty extends Widget<ManagedProperty, Properties, Configuration> {
    #name = text({
        label: "Имя",
        placeholder: "My Property",
        value: this.configuration.currentProperty.value?.name,
        fullWidth: true
    })
    .onTextChanged(name => this.setPropertyName(name));

    #textProperty = text({
        label: "Значение",
        placeholder: "My Value",
        fullWidth: true,
        value: this.configuration.currentProperty.value?.textProperty?.value
    })
    .onTextChanged(text => this.setTextProperty({value: text}));

    #resourceProperty = resourceProperty({
        ids: this.properties.resources.ids,
        resources: this.properties.resources.get,
        property: this.configuration.currentProperty.value?.resourceProperty
    });

    #textSourceButton = radio({checked: this.configuration.currentProperty.value.type == TEXT_PROPERTY})
    .onCheck(checked => {
        if (checked) {
            this.setPropertyType(TEXT_PROPERTY)
        }
    });

    #resourceSourceButton = radio({checked: this.configuration.currentProperty.value.type == RESOURCE_PROPERTY})
    .onCheck(checked => {
        if (checked) {
            this.setPropertyType(RESOURCE_PROPERTY)
        }
    });

    #versionSourceButton = radio({checked: this.configuration.currentProperty.value.type == VERSION_PROPERTY})
    .onCheck(checked => {
        if (checked) {
            this.setPropertyType(VERSION_PROPERTY)
        }
    });

    #renderSource = (sourceLabel: string, button: Widget<any>) =>
        horizontalGrid({spacing: 1, wrap: "nowrap", alignItems: "center"})
        .pushWidget(button)
        .pushWidget(label(sourceLabel));

    #sources = verticalGrid({spacing: 1, wrap: "nowrap"})
    .pushWidget(this.#renderSource("Текстовое свойство", this.#textSourceButton))
    .pushWidget(this.#renderSource("Свойство из ресурса", this.#resourceSourceButton))
    .pushWidget(this.#renderSource("Свойство из версии", this.#versionSourceButton));

    #asText = verticalGrid({spacing: 2})
    .pushWidget(this.#name)
    .pushWidget(this.#textProperty)
    .pushWidget(this.#sources);

    #asResource = verticalGrid({spacing: 2})
    .pushWidget(this.#name)
    .pushWidget(this.#resourceProperty)
    .pushWidget(this.#sources);

    #asVersion = verticalGrid({spacing: 2})
    .pushWidget(this.#name)
    .pushWidget(this.#sources);

    constructor(properties: Properties) {
        super(properties, Configuration);
        this.configuration.disabled.consume(disabled => {
            this.#name.setDisabled(disabled);
            this.#textProperty.setDisabled(disabled);
            this.#resourceProperty.setDisabled(disabled);
            this.#textSourceButton.setDisabled(disabled);
            this.#resourceSourceButton.setDisabled(disabled);
            this.#versionSourceButton.setDisabled(disabled);
        });
        this.configuration.duplicate.consume(duplicate => this.#name.setError({
            error: duplicate,
            text: "Свойства должны быть уникальны"
        }));
        this.configuration.currentProperty.consume(value => {
            switch (value?.type) {
                case TEXT_PROPERTY:
                    this.#textSourceButton.check();
                    this.#resourceSourceButton.uncheck();
                    this.#versionSourceButton.uncheck();
                    break;
                case RESOURCE_PROPERTY:
                    this.#resourceSourceButton.check();
                    this.#textSourceButton.uncheck();
                    this.#versionSourceButton.uncheck();
                    break;
                case VERSION_PROPERTY:
                    this.#versionSourceButton.check();
                    this.#textSourceButton.uncheck();
                    this.#resourceSourceButton.uncheck();
                    break;
            }
        });
    }

    useProperty = this.extract(configuration => configuration.currentProperty);

    getProperty = () => this.configuration.currentProperty.value;

    getPreviousProperty = () => this.configuration.currentProperty.previous;

    setProperty = (value: Property) => this.configuration.currentProperty.value = value;

    setPropertyName = (name: string) => this.setProperty({...this.getProperty(), name});

    setPropertyType = (type: string) => this.setProperty({...this.getProperty(), type});

    setTextProperty = (property: TextProperty) => this.setProperty({...this.getProperty(), textProperty: property});

    setResourceProperty = (property: ResourceProperty) => this.setProperty({...this.getProperty(), resourceProperty: property});

    onPropertyChange = (action: Dispatch<Property>) => this.useProperty(property => property.consume(action));


    useDisabled = this.extract(configuration => configuration.disabled);

    disabled = () => Boolean(this.configuration.disabled.value);

    setDisabled = (value: boolean) => this.useDisabled(disabled => disabled.value = value);

    disable = () => this.setDisabled(true);

    enable = () => this.setDisabled(false);


    useDuplicate = this.extract(configuration => configuration.duplicate);

    setDuplicate = (value: boolean) => this.useDuplicate(duplicate => duplicate.value = value);


    draw = () => {
        switch (this.getProperty()?.type) {
            case TEXT_PROPERTY:
                return this.#asText.render();
            case RESOURCE_PROPERTY:
                return this.#asResource.render();
            case VERSION_PROPERTY:
                return this.#asVersion.render();
        }
        throw new Error(`Unknown property type: ${this.getProperty()?.type}`)
    };
}

export const property = (properties: Properties) => new ManagedProperty(properties);
