import {ResourceAttribute, ResourceProperty} from "../../../model/ResourceProperty";
import {ResourceAttributeSelector, resourceAttributeSelector, ResourceSelector, resourceSelector} from "../common/PlatformSelectors";
import {verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {ManagedTextField, text} from "../../../framework/dsl/managed/ManagedTextField";
import {ARTIFACTS_RESOURCE, GIT_RESOURCE, OPEN_SHIFT_RESOURCE} from "../../../constants/ResourceConstants";
import {artifactsAttributes, gitAttributes, openShiftAttributes} from "../../../constants/ResourcesAttributes";
import {Configurable} from "../../../framework/pattern/Configurable";
import {ResourceIdentifier, Resources} from "../../../model/ResourceTypes";
import {lazy} from "../../../framework/pattern/Lazy";
import {Widget} from "../../../framework/widgets/Widget";

type Properties = {
    property?: ResourceProperty
    ids: ResourceIdentifier[]
    resources: Resources
}

class Configuration extends Configurable<Properties> {
    disabled = this.property(false);
}

export class ManagedResourceProperty extends Widget<ManagedResourceProperty, Properties, Configuration> {
    #resourceSelector: ResourceSelector;
    #attributeSelector: ResourceAttributeSelector;
    #valueText: ManagedTextField;

    useDisabled = this.extract(configuration => configuration.disabled);

    disabled = () => Boolean(this.configuration.disabled.value);

    setDisabled = (value: boolean) => this.useDisabled(disabled => disabled.value = value);

    disable = () => this.setDisabled(true);

    enable = () => this.setDisabled(false);

    constructor(properties: Properties) {
        super(properties, Configuration);

        const defaultResourceId = this.properties.property?.resourceId || this.properties.ids[0];
        const defaultAttributes = this.#getResourceAttributes(defaultResourceId);
        const defaultAttribute = this.properties.property
            ? {...this.properties.property} as ResourceAttribute
            : defaultAttributes[0];

        this.#valueText = text({
            fullWidth: true,
            value: defaultAttribute.value,
            disabled: true,
            password: Boolean(defaultAttribute?.isPassword.bind(defaultAttribute))
        });

        this.#attributeSelector = resourceAttributeSelector({
            attributes: defaultAttributes,
            selected: defaultAttribute
        })
        .onSelect((attribute: ResourceAttribute) => this.#valueText
        .setPassword(Boolean(attribute.isPassword))
        .setText(attribute.value));

        this.#resourceSelector = resourceSelector({
            ids: this.properties.ids,
            label: "Ресурс свойства",
            selected: this.properties.property?.resourceId
        })
        .onSelect(id => {
            this.#attributeSelector.setAvailableValues(this.#getResourceAttributes(id));
            this.#valueText.setText(this.#attributeSelector.selected().value);
            this.#valueText.setPassword(Boolean(this.#attributeSelector.selected().isPassword))
        });

        this.configuration.disabled.consume(disabled => {
            this.#attributeSelector.setDisabled(disabled);
            this.#resourceSelector.setDisabled(disabled);
        })
    }

    #getResourceAttributes = (id: ResourceIdentifier) => {
        switch (id.type) {
            case OPEN_SHIFT_RESOURCE:
                return openShiftAttributes(this.properties.resources.openShift.find(resource => resource.id == id.id)!);
            case GIT_RESOURCE:
                return gitAttributes(this.properties.resources.git.find(resource => resource.id == id.id)!);
            case ARTIFACTS_RESOURCE:
                return artifactsAttributes(this.properties.resources.artifacts.find(resource => resource.id == id.id)!);
        }
        return [] as ResourceAttribute[];
    };

    #property = lazy(() => verticalGrid({spacing: 2})
    .pushWidget(this.#resourceSelector)
    .pushWidget(verticalGrid({spacing: 2})
    .pushWidget(this.#attributeSelector)
    .pushWidget(this.#valueText)));

    draw = () => this.#property().render();
}

export const resourceProperty = (properties: Properties) => new ManagedResourceProperty(properties);
