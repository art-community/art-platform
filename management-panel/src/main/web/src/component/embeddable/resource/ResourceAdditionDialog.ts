import {ARTIFACTS_RESOURCE, GIT_RESOURCE, OPEN_SHIFT_RESOURCE, PROXY_RESOURCE} from "../../../constants/ResourceConstants";
import {ManagedTextField, text} from "../../../framework/dsl/managed/ManagedTextField";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {ENTITY_NAME_REGEX} from "../../../constants/Regexps";
import {Closable} from "../../../framework/pattern/Optional";
import {useResourceApi} from "../../../api/ResourceApi";
import {openShiftResourceFields} from "./OpenShiftResourceFields";
import {gitResourceFields} from "./GitResourceFields";
import {artifactsResourceFields} from "./ArtifactsResourceFields";
import {proxyResourceFields} from "./ProxyResourceFields";
import {ArtifactsResource, GitResource, OpenShiftResource, ProxyResource, ResourceIdentifier} from "../../../model/ResourceTypes";
import {verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {dialog} from "../../../framework/dsl/managed/ManagedDialog";
import {StaticWidget} from "../../../framework/widgets/Widget";
import {handleEnter} from "../../../framework/constants/Constants";
import {form} from "../../../framework/widgets/Form";

type Properties = {
    type: string
}

class ResourceAdditionDialog extends StaticWidget<ResourceAdditionDialog, Properties> implements Closable {
    #resourceIds: ResourceIdentifier[] = [];
    #api = this.hookValue(useResourceApi);

    #createFields = () => {
        switch (this.properties.type) {
            case OPEN_SHIFT_RESOURCE:
                return openShiftResourceFields();
            case GIT_RESOURCE:
                return gitResourceFields();
            case ARTIFACTS_RESOURCE:
                return artifactsResourceFields();
            case PROXY_RESOURCE:
                return proxyResourceFields();
        }
        return new Map<string, ManagedTextField>();
    }

    #fields = this.#createFields()

    #validate = () => {
        this.#validateDuplicates();
        const empties = !this.#name.text() || this.#fields.some((name, value) => value.required() && !value.text());
        const errors = this.#name.error() || this.#fields.some((name, value) => value.error());
        const disabled = empties || errors;
        this.#button.setDisabled(disabled)
    };

    #validateDuplicates = () => {
        if (!this.#resourceIds.some(id => id.type == this.properties.type && id.name == this.#name.text())) {
            return;
        }
        this.#name.setError({
            error: true,
            text: "Ресурс с таким именем уже существует"
        })
    };

    #addResource = () => {
        const api = this.#api();
        let properties = {};
        this.#fields.forEach((field, name) => properties[name] = field.value())
        switch (this.properties.type) {
            case OPEN_SHIFT_RESOURCE:
                api.addOpenShiftResource({
                    name: this.#name.text(),
                    ...properties
                } as OpenShiftResource, this.#dialog.close);
                return;
            case GIT_RESOURCE:
                api.addGitResource({
                    name: this.#name.text(),
                    ...properties
                } as GitResource, this.#dialog.close);
                return;
            case ARTIFACTS_RESOURCE:
                api.addArtifactsResource({
                    name: this.#name.text(),
                    ...properties
                } as ArtifactsResource, this.#dialog.close);
                return;
            case PROXY_RESOURCE:
                api.addProxyResource({
                    name: this.#name.text(),
                    ...properties
                } as ProxyResource, this.#dialog.close);
                return;
        }
    };

    #button = button({
        color: "primary",
        variant: "contained",
        disabled: true,
        label: "Добавить"
    })
    .onClick(this.#addResource)
    .onClick(() => this.#dialog.close());

    #name = text({
        label: "Имя",
        fullWidth: true,
        required: true,
        placeholder: "my-resource",
        autoFocus: true,
        regexp: ENTITY_NAME_REGEX,
        defaultErrorText: "Имя ресурса не должно быть пустым и содержать только символы [0-9a-z-.]"
    })
    .onTextChanged(this.#validate);

    #dialogLabel = () => {
        switch (this.properties.type) {
            case OPEN_SHIFT_RESOURCE:
                return "Новый OpenShift"
            case GIT_RESOURCE:
                return "Новый Git репозиторий"
            case ARTIFACTS_RESOURCE:
                return "Новое хранилище артефактов"
            case PROXY_RESOURCE:
                return "Новый прокси"
        }
    }

    #dialog = dialog({
        label: this.#dialogLabel(),
        visible: true,
        fullWidth: true,
        onKeyDown: handleEnter(this.#button.click)
    })
    .widget(form(verticalGrid({spacing: 1}).pushWidget(this.#name).pushWidgets(this.#fields.valuesToArray())))
    .actions([this.#button], {justify: "flex-end"})
    .onClose(() => {
        this.#name.clear()
        this.#fields.forEach(value => value.clear())
    });

    constructor(properties: Properties) {
        super(properties);
        this.onLoad(() => this.#api().getResourceIds(ids => {
            this.#resourceIds = ids;
            this.#validateDuplicates();
        }));
        this.#fields.forEach(field => field.onTextChanged(this.#validate));
    }

    onClose = this.#dialog.onClose;

    draw = this.#dialog.render;
}

export const resourceAdditionDialog = (type: string) => new ResourceAdditionDialog({type});
