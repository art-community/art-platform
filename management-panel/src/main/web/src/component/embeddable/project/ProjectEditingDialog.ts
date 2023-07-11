import {text} from "../../../framework/dsl/managed/ManagedTextField";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {useProjectApi} from "../../../api/ProjectApi";
import {ENTITY_NAME_REGEX} from "../../../constants/Regexps";
import {Project} from "../../../model/ProjectTypes";
import {Closable} from "../../../framework/pattern/Optional";
import {dialog} from "../../../framework/dsl/managed/ManagedDialog";
import {StaticWidget} from "../../../framework/widgets/Widget";
import {handleEnter} from "../../../framework/constants/Constants";
import {conditional} from "../../../framework/pattern/Conditional";
import {projectOpenShiftConfigurator} from "./ProjectOpenShiftConfigurator";
import {OPEN_SHIFT_RESOURCE} from "../../../constants/ResourceConstants";
import {verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {projectNotificationsConfigurator} from "./ProjectNotificationsConfigurator";
import {ResourcesStore} from "../../../loader/ResourcesLoader";

type Properties = {
    project: Project
    resources: ResourcesStore
}

class ProjectEditingDialog extends StaticWidget<ProjectEditingDialog, Properties> implements Closable {
    #api = this.hookValue(useProjectApi);
    #projectNames: string[] = [];

    #validate = () => {
        this.#validateDuplicates();
        const empties = !this.#name.text();
        const errors = this.#name.error();
        const disabled = empties || errors;
        this.#button.setDisabled(disabled)
    };

    #validateDuplicates = () => {
        if (this.#name.text() != this.properties.project.name && this.#projectNames.includes(this.#name.text())) {
            this.#name.setError({
                error: true,
                text: "Проект с таким именем уже существует"
            })
        }
    };

    #saveProject = () => this.#api().updateProject({
        id: this.properties.project.id,
        name: this.#name.text(),
        openShiftConfiguration: this.#openShiftConfigurator.get()?.configure(),
        notificationsConfiguration: this.#notificationsConfigurator.configure()
    });

    #isResourceType = (type: string) => () => this.properties.project.externalId.resourceId.type == type;

    #button = button({
        fullWidth: true,
        color: "primary",
        variant: "contained",
        disabled: true,
        label: "Сохранить"
    })
    .onClick(this.#saveProject)
    .onClick(() => this.#dialog.close());

    #name = text({
        label: "Имя",
        value: this.properties.project.name,
        placeholder: "my-project",
        fullWidth: true,
        required: true,
        autoFocus: true,
        regexp: ENTITY_NAME_REGEX,
        defaultErrorText: "Имя проекта не должно быть пустым и содержать только символы [0-9a-z-.]"
    })
    .onTextChanged(this.#validate);

    #openShiftConfigurator = conditional(() => this.#isResourceType(OPEN_SHIFT_RESOURCE))
    .persist(() => projectOpenShiftConfigurator(this.properties.project.openShiftConfiguration)
    .onChange(this.#validate));

    #notificationsConfigurator = projectNotificationsConfigurator(this.properties.resources, this.properties.project.notificationsConfiguration)
    .onChange(this.#validate);

    #configuration = verticalGrid({spacing: 2, wrap: "nowrap"})
    .breakpoints({xs: true})
    .pushWidget(this.#name)
    .pushWidget(this.#notificationsConfigurator)
    .pushWidget(this.#openShiftConfigurator);

    #dialog = dialog({
        label: `Изменить проект ${this.properties.project.name}`,
        visible: true,
        onKeyDown: handleEnter(() => this.#button.click())
    })
    .widget(this.#configuration)
    .action(this.#button, {justify: "flex-end"})
    .onClose(this.#name.clear);

    constructor(properties: Properties) {
        super(properties);
        this.onLoad(() => this.#api().getProjectNames(names => {
            this.#projectNames = names;
            this.#validateDuplicates();
        }));
    }

    onClose = this.#dialog.onClose;

    draw = this.#dialog.render;
}

export const projectEditingDialog = (resources: ResourcesStore, project: Project) => new ProjectEditingDialog({resources, project});
