import {Variable} from "../../../model/Variable";
import {collection, collectionItem} from "../../../framework/dsl/managed/ManagedCollection";
import {horizontalGrid, ManagedGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {ManagedTextField, text} from "../../../framework/dsl/managed/ManagedTextField";
import {PORT_REGEX, VARIABLE_NAME_REGEX, VARIABLE_VALUE_REGEX} from "../../../constants/Regexps";
import {Property} from "../../../model/Property";
import {ManagedProperty, property} from "../property/ManagedProerty";
import {Dispatch} from "react";
import {ResourcesStore} from "../../../loader/ResourcesLoader";
import {GridDirection} from "@material-ui/core";
import {CodeEditorTheme, identity} from "../../../framework/constants/Constants";
import {codeEditor, ManagedCodeEditor} from "../../../framework/dsl/managed/ManagedCodeEditor";
import {DEFAULT_PORT} from "../../../constants/NetworkConstants";

type VariablesProperties = {
    label: string
    nameLabel: string
    valueLabel: string
    variables?: Variable[]
}
type StringCollectionProperties = {
    strings?: string[]
    itemLabel?: string
    collectionLabel?: string
    direction?: GridDirection
    placeholder?: string
    regexp?: RegExp
    mask?: RegExp,
    duplicateMessage?: string
    labelDivider?: boolean
    decorator?: (field: ManagedTextField) => ManagedTextField
}
type KeyValueCollectionProperties = {
    pairs?: { key: string, value: string }[]
    keyLabel?: string
    valueLabel?: string
    collectionLabel?: string
    direction?: GridDirection
    keyPlaceholder?: string
    keyRegexp?: RegExp
    valuePlaceholder?: string
    valueRegexp?: RegExp
    keyMask?: RegExp,
    valueMask?: RegExp,
    duplicateMessage?: string
    labelDivider?: boolean
    keyDecorator?: (field: ManagedTextField) => ManagedTextField
    valueDecorator?: (field: ManagedTextField) => ManagedTextField
}
type FileCollectionProperties = {
    files?: { name: string, content: string }[]
    collectionLabel?: string
    direction?: GridDirection
    placeholder?: string
    regexp?: RegExp
    error?: string
    mask?: RegExp,
    duplicateMessage?: string
    labelDivider?: boolean
    nameDecorator?: (field: ManagedTextField) => ManagedTextField
    contentDecorator?: (field: ManagedCodeEditor) => ManagedCodeEditor
    editorTheme?: CodeEditorTheme
    editorHeight?: number | string
    addButtonTooltip?: string
    deleteButtonTooltip?: string
}
type PortsCollectionProperties = {
    ports?: number[]
    decorator?: (port: ManagedTextField) => ManagedTextField
}
type PropertiesCollectionProperties = {
    properties: Property[]
    resources: ResourcesStore
    decorator?: (property: ManagedProperty) => ManagedProperty
}

export const environmentVariablesCollection = (variables: Variable[] = []) => collection({
    ids: variables.map((_, index) => index),
    label: "Переменные среды",
    factory: id => {
        const name = text({
            label: "Имя",
            value: variables[id]?.name,
            placeholder: "VARIABLE_NAME",
            regexp: VARIABLE_NAME_REGEX
        });
        const value = text({
            label: "Значение",
            value: variables[id]?.value,
            placeholder: "VALUE_REGEX",
            regexp: VARIABLE_VALUE_REGEX
        });
        const item = verticalGrid({spacing: 1})
        .pushWidget(name)
        .pushWidget(value);
        return collectionItem({
            widget: item,
            valueExtractor: name.text,
            valueConsumer: name.onTextChanged,
            duplicateHandler: duplicate => name.setError({
                error: duplicate,
                text: "Имена переменных должны быть уникальны"
            }),
            disableHandler: disabled => {
                name.setDisabled(disabled);
                value.setDisabled(disabled)
            },
            validator: () => !name.text()
        })
    }
});

export const variablesCollection = (properties: VariablesProperties) => {
    const {label, nameLabel, valueLabel, variables = []} = properties;
    return collection<ManagedGrid>({
        ids: variables?.map((_, index) => index),
        label: label,
        factory: id => {
            const name = text({
                label: nameLabel,
                fullWidth: true,
                value: variables[id]?.name,
                placeholder: "NAME",
                regexp: VARIABLE_NAME_REGEX,
                required: true
            });
            const value = text({
                label: valueLabel,
                fullWidth: true,
                value: variables[id]?.value,
                placeholder: "VALUE"
            });
            const item = verticalGrid({spacing: 1})
            .pushWidget(name)
            .pushWidget(value);
            return collectionItem({
                widget: item,
                valueExtractor: name.text,
                valueConsumer: name.onTextChanged,
                duplicateHandler: duplicate => name.setError({
                    error: duplicate,
                    text: "Имена должны быть уникальны"
                }),
                disableHandler: disabled => {
                    name.setDisabled(disabled);
                    value.setDisabled(disabled)
                },
                validator: () => !name.text()
            })
        }
    });
};

export const stringCollection = (properties: StringCollectionProperties) => {
    const {
        itemLabel,
        collectionLabel,
        direction,
        strings = [],
        placeholder,
        regexp,
        duplicateMessage,
        mask,
        decorator,
        labelDivider
    } = properties;
    return collection<ManagedTextField>({
        ids: strings.map((_, index) => index),
        labelDivider,
        direction,
        label: collectionLabel,
        factory: id => {
            const itemDecorator = decorator || identity;
            const string = itemDecorator(text({
                label: itemLabel,
                fullWidth: true,
                value: strings[id],
                placeholder,
                regexp,
                mask,
                required: true
            }));
            return collectionItem({
                widget: string,
                valueExtractor: string.text,
                valueConsumer: string.onTextChanged,
                duplicateHandler: duplicate => string.setError({
                    error: duplicate,
                    text: duplicateMessage || "Значения должны быть уникальны"
                }),
                disableHandler: string.setDisabled,
                validator: () => !string.text()
            })
        }
    });
};

export const keyValueCollection = (properties: KeyValueCollectionProperties) => {
    const {
        keyLabel,
        valueLabel,
        collectionLabel,
        direction,
        pairs = [],
        keyPlaceholder,
        valuePlaceholder,
        keyRegexp,
        valueRegexp,
        duplicateMessage,
        keyMask,
        valueMask,
        keyDecorator,
        valueDecorator,
        labelDivider
    } = properties;
    return collection<ManagedGrid>({
        ids: pairs.map((_, index) => index),
        labelDivider,
        direction,
        label: collectionLabel,
        factory: id => {
            const decorateKey = keyDecorator || identity;
            const decorateValue = valueDecorator || identity;
            const key = decorateKey(text({
                label: keyLabel,
                fullWidth: true,
                value: pairs[id]?.key,
                placeholder: keyPlaceholder,
                regexp: keyRegexp,
                mask: keyMask,
                required: true
            }));
            const value = decorateValue(text({
                label: valueLabel,
                fullWidth: true,
                value: pairs[id]?.value,
                placeholder: valuePlaceholder,
                regexp: valueRegexp,
                mask: valueMask,
                required: true
            }));
            return collectionItem({
                widget: horizontalGrid({spacing: 1})
                .breakpoints({xs: true})
                .pushWidget(key)
                .pushWidget(value),
                valueExtractor: key.text,
                valueConsumer: key.onTextChanged,
                duplicateHandler: duplicate =>
                    key.setError({
                        error: duplicate,
                        text: duplicateMessage || "Имена должны быть уникальны"
                    }),
                disableHandler: disabled => {
                    key.setDisabled(disabled)
                    value.setDisabled(disabled)
                },
                validator: () => !key.text()
            })
        }
    });
};

export const fileCollection = (properties: FileCollectionProperties) => {
    const {
        collectionLabel,
        direction,
        files = [],
        placeholder,
        regexp,
        duplicateMessage,
        mask,
        nameDecorator,
        contentDecorator,
        labelDivider,
        editorTheme,
        error,
        editorHeight,
        addButtonTooltip,
        deleteButtonTooltip,
    } = properties;
    return collection<ManagedGrid>({
        ids: files.map((_, index) => index),
        labelDivider,
        direction,
        label: collectionLabel,
        addButtonTooltip,
        deleteButtonTooltip,
        factory: id => {
            const decorateName = nameDecorator || identity;
            const decorateContent = contentDecorator || identity;
            const fileName = decorateName(text({
                label: "Имя",
                fullWidth: true,
                value: files[id]?.name,
                placeholder: placeholder,
                regexp: regexp,
                mask: mask,
                required: true,
                defaultErrorText: error
            }));
            const fileContent = decorateContent(codeEditor({
                themeName: editorTheme,
                height: editorHeight,
                fileName: fileName.text(),
                value: files[id]?.content
            }));
            fileName.onTextChanged(fileContent.setFileName)
            return collectionItem({
                widget: verticalGrid({spacing: 1, wrap: "nowrap"})
                .breakpoints({xs: true})
                .pushWidget(fileName)
                .pushWidget(fileContent),
                valueExtractor: fileName.text,
                valueConsumer: fileName.onTextChanged,
                duplicateHandler: duplicate =>
                    fileName.setError({
                        error: duplicate,
                        text: duplicateMessage || "Имена должны быть уникальны"
                    }),
                disableHandler: disabled => {
                    fileName.setDisabled(disabled)
                    fileContent.setReadOnly(disabled)
                },
                validator: () => !fileName.text() || fileName.error()
            })
        }
    });
};

export const portsCollection = (properties: PortsCollectionProperties) => collection<ManagedTextField>({
    ids: (properties.ports || []).map((_, index) => index),
    factory: id => {
        const item = (properties.decorator || identity)(text({
            label: "Порт",
            value: (properties.ports || [])[id],
            placeholder: DEFAULT_PORT,
            regexp: PORT_REGEX,
            mask: PORT_REGEX,
            required: true,
            fullWidth: true
        }))
        .useText(text => text.prevent(port => !port?.startsWith("0")));
        return collectionItem({
            widget: item,
            valueExtractor: item.text,
            valueConsumer: item.onTextChanged,
            duplicateHandler: duplicate => item.setError({
                error: duplicate,
                text: "Порты должны быть уникальны"
            }),
            disableHandler: item.setDisabled,
            validator: () => !item.text()
        })
    }
});

export const propertiesCollection = (properties: PropertiesCollectionProperties) => collection<ManagedProperty>({
    ids: properties.properties.map((_, index) => index),
    labelDivider: true,
    factory: id => {
        const item = (properties.decorator || identity)(property({property: properties.properties[id], resources: properties.resources}));
        return collectionItem({
            widget: item,
            valueExtractor: () => item.getProperty()?.name,
            valueConsumer: (action: Dispatch<string>) => item.onPropertyChange(property => {
                if (property.name != item.getPreviousProperty()?.name) {
                    action(property.name);
                }
            }),
            validator: () => !item.getProperty()?.name,
            duplicateHandler: item.setDuplicate,
            disableHandler: item.setDisabled,
        })
    }
});
