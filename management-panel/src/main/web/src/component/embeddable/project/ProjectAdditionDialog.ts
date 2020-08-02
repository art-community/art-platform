import {EXECUTORS, GIT_RESOURCE, OPEN_SHIFT_RESOURCE} from "../../../constants/ResourceConstants";
import {verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {resourceSelector} from "../common/PlatformSelectors";
import {text} from "../../../framework/dsl/managed/ManagedTextField";
import {dialog} from "../../../framework/dsl/managed/ManagedDialog";
import {group} from "../../../framework/dsl/simple/SimpleGroup";
import {button} from "../../../framework/dsl/managed/ManagedButton";
import {useProjectApi} from "../../../api/ProjectApi";
import {divider} from "../../../framework/dsl/simple/SimpleDivider";
import {ENTITY_NAME_REGEX} from "../../../constants/Regexps";
import {Closable} from "../../../framework/pattern/Optional";
import {StaticWidget} from "../../../framework/widgets/Widget";
import {ResourcesStore} from "../../../loader/ResourcesLoader";
import {conditional} from "../../../framework/pattern/Conditional";
import {projectOpenShiftConfigurator} from "./ProjectOpenShiftConfigurator";
import {platform} from "../../entry/EntryPoint";
import {projectNotificationsConfigurator} from './ProjectNotificationsConfigurator';

type Properties = {
    resources: ResourcesStore
}

class ProjectAdditionDialog extends StaticWidget<ProjectAdditionDialog, Properties> implements Closable {
    #projectNames: string[] = [];
    #api = this.hookValue(useProjectApi);

    #validate = () => {
        this.#validateDuplicates();
        const empties = !this.#name.text();
        const errors = this.#name.error();
        const disabled = empties || errors;
        this.#button.setDisabled(disabled)
    };

    #validateDuplicates = () => {
        if (!this.#projectNames.includes(this.#name.text())) {
            return;
        }
        this.#name.setError({
            error: true,
            text: "Проект с таким именем уже существует"
        })
    };

    #addProject = () => this.#api().addProject({
        name: this.#name.text(),
        gitResourceId: this.#gitSelector.selected().id,
        initializationResourceId: this.#initializationSelector.selected(),
        openShiftConfiguration: this.#openShiftConfigurator.get()?.configure(),
        notificationsConfiguration: this.#notificationsConfigurator.configure()
    });

    #isSelectedResourceType = (type: string) => () => this.#initializationSelector.selected()?.type == type;

    #button = button({
        color: "primary",
        variant: "contained",
        disabled: true,
        label: "Добавить"
    })
    .onClick(this.#addProject)
    .onClick(() => this.#dialog.close());

    #name = text({
        label: "Имя",
        fullWidth: true,
        placeholder: "my-project",
        autoFocus: true,
        required: true,
        regexp: ENTITY_NAME_REGEX,
        defaultErrorText: "Имя проекта не должно быть пустым и содержать только символы [0-9a-z-.]"
    })
    .onTextChanged(this.#validate);

    #gitSelector = resourceSelector({
        ids: this.properties.resources.idsOf([GIT_RESOURCE]),
        label: "Git репозиторий"
    });

    #initializationSelector = resourceSelector({
        ids: this.properties.resources.idsOf(EXECUTORS),
        label: "Ресурс для инициализации"
    })
    .onSelect(() => this.#openShiftConfigurator.notify())

    #initializationConfiguration = verticalGrid({spacing: 1})
    .breakpoints({xs: true})
    .pushWidget(this.#initializationSelector)

    #configuration = verticalGrid({spacing: 2, wrap: "nowrap"})
    .breakpoints({xs: true})
    .pushWidget(this.#gitSelector)
    .pushWidget(this.#initializationConfiguration);

    #openShiftConfigurator = conditional(() => this.#isSelectedResourceType(OPEN_SHIFT_RESOURCE))
    .persist(projectOpenShiftConfigurator);

    #notificationsConfigurator = projectNotificationsConfigurator(this.properties.resources);

    #form = group()
    .widget(this.#name)
    .widget(divider())
    .widget(this.#configuration)
    .widget(divider())
    .widget(this.#notificationsConfigurator)
    .widget(divider())
    .widget(this.#openShiftConfigurator);

    #dialog = dialog({
        label: "Новый проект",
        visible: true,
        fullWidth: true
    })
    .widget(this.#form)
    .action(this.#button, {justify: "flex-end"})
    .onClose(() => {
        this.#name.clear()
        this.#gitSelector.reset()
        this.#initializationSelector.reset()
    });

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

export const projectAdditionDialog = (resources: ResourcesStore) => new ProjectAdditionDialog({resources});
