import {Widget} from "../../../framework/widgets/Widget";
import {verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {useUserApi} from "../../../api/UserApi";
import {userCard} from "./UserCard";

class UsersTab extends Widget<UsersTab> {
    #api = this.hookValue(useUserApi)

    #cards = verticalGrid({spacing: 1})

    constructor() {
        super();
        this.onLoad(() => this.#api().getUsers(users => this.#cards.pushWidgets(users.filter(user => !user.admin).map(userCard))))
    }

    draw = this.#cards.render
}

export const usersTab = () => new UsersTab();
