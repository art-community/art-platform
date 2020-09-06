import {button} from "../../../framework/dsl/managed/ManagedButton";
import {Closable} from "../../../framework/pattern/Optional";
import {dialog} from "../../../framework/dsl/managed/ManagedDialog";
import {Widget} from "../../../framework/widgets/Widget";
import {handleEnter} from "../../../framework/constants/Constants";
import {User} from "../../../model/UserTypes";
import {useUserApi} from "../../../api/UserApi";
import {ManagedGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {SIDE_BAR_ACTIONS} from "../../../constants/SideBarActions";
import {checkbox} from "../../../framework/dsl/managed/ManagedCheckbox";
import {Configurable} from "../../../framework/pattern/Configurable";
import {useProjectApi} from "../../../api/ProjectApi";
import {Project} from "../../../model/ProjectTypes";
import {conditional} from "../../../framework/pattern/Conditional";
import {isNotEmptyArray} from "../../../framework/extensions/extensions";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {useNotifications} from "../../../framework/hooks/Hooks";
import {ADMINISTRATION, PROJECTS_MANAGEMENT} from "../../../constants/UserActions";
import {act} from "react-dom/test-utils";

type Properties = {
    user: User
}

class Configuration extends Configurable<Properties> {
    user = this.property<User>()

    projects = this.property<Project[]>([])
}

class UserPrivilegesEditingDialog extends Widget<UserPrivilegesEditingDialog, Properties, Configuration> implements Closable {
    #userApi = this.hookValue(useUserApi);

    #projectApi = this.hookValue(useProjectApi);

    #notifications = this.hookValue(useNotifications);

    #saveUser = () => this.#userApi().updateUser(this.configuration.user.value!, () => {
        this.#notifications().info(`Пользователь ${this.configuration.user.value!.name} успешно обновлен`)
        this.#dialog.close()
    });

    #actions = conditional(() => this.configuration.user.value)
    .persist(() => verticalGrid({spacing: 1})
    .pushWidgets(SIDE_BAR_ACTIONS
    .filter(action => action.action != PROJECTS_MANAGEMENT.action && action.action != ADMINISTRATION.action)
    .map(action => checkbox({
            label: action.text,
            checked: this.configuration.user.value!.availableActions.includes(action.action)
        })
        .onCheck(checked => {
                const user = this.configuration.user.value!;
                if (checked) {
                    user.availableActions.push(action.action)
                    this.configuration.user.value = user
                    return
                }
                user.availableActions = user.availableActions.withOut(action.action)
                this.configuration.user.value = user
            }
        )
    )))

    #projects = conditional(() => this.configuration.user.value && isNotEmptyArray(this.configuration.projects.value))
    .persist(() => verticalGrid({spacing: 1})
        .pushWidgets(this.configuration.projects.value.map(project => checkbox({
                label: project.name,
                checked: this.configuration.user.value!.availableProjects.includes(project.id)
            })
            .onCheck(checked => {
                    const user = this.configuration.user.value!;
                    if (checked) {
                        user.availableProjects.push(project.id)
                        this.configuration.user.value = user
                        return
                    }
                    user.availableProjects = user.availableProjects.withOut(project.id)
                    this.configuration.user.value = user
                }
            )
        ))
    )

    #content = conditional(() => this.configuration.user.value)
    .persist(() => verticalGrid({spacing: 1})
    .pushWidget(label({text: "Действия", variant: "h6", color: "secondary"}))
    .pushWidget(this.#actions)
    .pushWidget(label({text: "Проекты", variant: "h6", color: "secondary"}))
    .pushWidget(this.#projects))

    #button = button({
        fullWidth: true,
        color: "primary",
        variant: "contained",
        label: "Сохранить"
    })
    .onClick(this.#saveUser);

    #dialog = dialog({
        label: `Настроить права пользователя ${this.properties.user.name}`,
        visible: true,
        onKeyDown: handleEnter(() => this.#button.click())
    })
    .widget(this.#content)
    .action(this.#button, {justify: "flex-end"});

    constructor(properties: Properties) {
        super(properties, Configuration);
        this.onLoad(() => {
            this.#userApi().getUser(this.properties.user.id, user => {
                this.configuration.user.set(user);
                this.#content.notify()
                this.#actions.notify()
                this.#projects.notify()
            })
            this.#projectApi().getProjects(projects => {
                this.configuration.projects.set(projects)
                this.#projects.notify()
            })
        })
    }

    onClose = this.#dialog.onClose;

    draw = this.#dialog.render;
}

export const userPrivilegesEditingDialog = (user: User) => new UserPrivilegesEditingDialog({user});
