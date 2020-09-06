import {text} from "../../../../framework/dsl/managed/ManagedTextField";
import {resourceSelector} from "../../common/PlatformSelectors";
import {ResourceIdentifier} from "../../../../model/ResourceTypes";
import {DispatchWithoutAction} from "react";
import {ApplicationField} from "../ApplicationField";

const filebeatUrl = () => text({
    label: "Filebeat URL",
    fullWidth: true,
    required: true,
    placeholder: "filebeat"
})

const resourceId = (resourceIds: ResourceIdentifier[]) => resourceSelector({ids: resourceIds})

export const filebeatApplicationFields = (resourceIds: ResourceIdentifier[]) => {
    const url = filebeatUrl();
    const resourceIdSelector = resourceId(resourceIds);

    const urlField = {
        widget: url,
        clear: url.clear,
        error: url.error,
        value: url.text,
        setValue: (value: string) => {
            url.setText(value);
            return urlField;
        },
        onChange: (action: DispatchWithoutAction) => {
            url.onTextChanged(action)
            return urlField;
        }
    };

    const resourceIdField = {
        widget: resourceIdSelector,
        clear: resourceIdSelector.reset,
        value: resourceIdSelector.selected,
        error: () => false,
        setValue: (value: ResourceIdentifier) => {
            resourceIdSelector.select(value);
            return resourceIdField;
        },
        onChange: (action: DispatchWithoutAction) => {
            resourceIdSelector.onSelect(action)
            return resourceIdField;
        }
    };

    return new Map<string, ApplicationField<unknown>>()
    .with("url", urlField)
    .with("resourceId", resourceIdField);
};
