import * as React from "react";
import moment from "moment"
import {Configurable} from "../../../framework/pattern/Configurable";
import {card} from "../../../framework/dsl/managed/card/ManagedCard";
import {CardMenuProperties} from "../../../framework/dsl/managed/card/CardMenu";
import {CardAttributeProperties} from "../../../framework/dsl/managed/card/CardAttribute";
import {group} from "../../../framework/dsl/simple/SimpleGroup";
import {Widget} from "../../../framework/widgets/Widget";
import {DATE_TIME_FORMAT} from "../../../constants/DateTimeConstants";
import {User} from "../../../model/UserTypes";
import HttpsOutlined from "@material-ui/icons/HttpsOutlined";
import {proxy} from "../../../framework/widgets/Proxy";
import {optional} from "../../../framework/pattern/Optional";
import {userPrivilegesEditingDialog} from './UserPrivilegesEditingDialog';

type Properties = {
    user: User
}

class Configuration extends Configurable<Properties> {
    user = this.property(this.defaultProperties.user);
}

class UserCard extends Widget<UserCard, Properties, Configuration> {
    #avatar = () => ({letter: {firstLetter: this.configuration.user.value!.name[0]}});

    #menu = (): CardMenuProperties => ({
        actions: {
            buttons: [
                {
                    tooltip: "Привилегии",
                    icon: proxy(<HttpsOutlined color={"primary"}/>),
                    onClick: () => this.#userPrivilegesEditingDialog.spawn()
                }
            ]
        }
    })

    #attributes = () => {
        const {
            email,
            fullName,
            name,
            token,
            updateTimeStamp
        } = this.configuration.user.value!;
        const attributes: CardAttributeProperties[] = [];

        attributes
        .with({name: "Имя", value: name})
        .with({name: "ФИО", value: fullName})
        .with({name: "Почта", value: email})
        .with({name: "Токен", value: token})
        .with({name: "Метка обновления", value: moment.unix(updateTimeStamp).format(DATE_TIME_FORMAT)})

        return attributes;
    };

    #userPrivilegesEditingDialog = this.add(optional(() => userPrivilegesEditingDialog(this.properties.user)))

    #card = card({label: this.configuration.user.value!.name})
    .configureAvatar(avatar => avatar.setAvatar(this.#avatar()))
    .configureMenu(menu => menu.setMenu(this.#menu()))
    .setAttributes(this.#attributes());

    constructor(properties: Properties) {
        super(properties, Configuration);
        const id = this.properties.user.id;
        this.widgetName = `(${this.constructor.name}): ${id}`
    }

    key = () => this.properties.user.id;

    draw = this.#card.render;
}

export const userCard = (user: User) => new UserCard({user});
