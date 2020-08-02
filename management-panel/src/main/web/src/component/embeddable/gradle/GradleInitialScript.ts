import {GRADLE_GROOVY_SCRIPT_FORMAT, GRADLE_KTS_SCRIPT_FORMAT} from "../../../constants/GradleConstants";
import {Configurable} from "../../../framework/pattern/Configurable";
import {checkBoxPanel} from "../../../framework/dsl/managed/ManagedPanel";
import {horizontalGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {radio} from "../../../framework/dsl/managed/ManagedRadio";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {codeEditor} from "../../../framework/dsl/managed/ManagedCodeEditor";
import {lazy} from "../../../framework/pattern/Lazy";
import {platform} from "../../entry/EntryPoint";
import {Widget} from "../../../framework/widgets/Widget";
import {event} from "../../../framework/pattern/Event";
import {DispatchWithoutAction} from "react";
import {CodeEditorTheme} from "../../../framework/constants/Constants";
import {PlatformTheme} from "../../../constants/PlatformTheme";

type Properties = {
    groovyContent?: string
    kotlinContent?: string
    format?: string
}

class Configuration extends Configurable<Properties> {
    groovyContent = this.property(this.defaultProperties.groovyContent)

    kotlinContent = this.property(this.defaultProperties.kotlinContent)

    format = this.property(this.defaultProperties.format)

    change = event();
}

class GradleInitialScript extends Widget<GradleInitialScript, Properties, Configuration> {
    #editor = lazy(() => codeEditor({
        language: this.configuration.format.value == GRADLE_KTS_SCRIPT_FORMAT
            ? "kotlin"
            : "groovy",
        value: this.configuration.format.value == GRADLE_KTS_SCRIPT_FORMAT
            ? this.configuration.kotlinContent.value
            : this.configuration.groovyContent.value,
        themeName: platform.themeName() == PlatformTheme.DARK ? CodeEditorTheme.DARK : CodeEditorTheme.LIGHT
    })
    .useText(text => {
        text.consume(content => this.configuration.format.value == GRADLE_KTS_SCRIPT_FORMAT
            ? this.configuration.kotlinContent.set(content)
            : this.configuration.groovyContent.set(content))
        this.configuration.format.consume(format => format == GRADLE_KTS_SCRIPT_FORMAT
            ? text.set(this.configuration.kotlinContent.value)
            : text.set(this.configuration.groovyContent.value)
        )
    })
    .onTextChanged(this.configuration.change.execute));

    #groovyRadio = radio({
        color: "primary",
        checked: this.configuration.format.value == GRADLE_GROOVY_SCRIPT_FORMAT
    })
    .onCheck(checked => checked && this.configuration.format.set(GRADLE_GROOVY_SCRIPT_FORMAT))
    .onCheck(this.configuration.change.execute)
    .useChecked(checked => this.configuration.format.consume(format => checked.set(format == GRADLE_GROOVY_SCRIPT_FORMAT)));

    #kotlinRadio = radio({
        color: "primary",
        checked: this.configuration.format.value == GRADLE_KTS_SCRIPT_FORMAT
    })
    .onCheck(checked => checked && this.configuration.format.set(GRADLE_KTS_SCRIPT_FORMAT))
    .onCheck(this.configuration.change.execute)
    .useChecked(checked => this.configuration.format.consume(format => checked.set(format == GRADLE_KTS_SCRIPT_FORMAT)));

    #radios = verticalGrid({wrap: "nowrap"})
    .pushWidget(horizontalGrid({wrap: "nowrap", spacing: 1, alignItems: "center"})
        .pushWidget(this.#groovyRadio)
        .pushWidget(label({color: "secondary", noWrap: true, text: "Groovy"}))
    )
    .pushWidget(horizontalGrid({wrap: "nowrap", spacing: 1, alignItems: "center"})
        .pushWidget(this.#kotlinRadio)
        .pushWidget(label({color: "secondary", noWrap: true, text: "Kotlin"}))
    );

    #script = lazy(() => {
        platform
        .onThemeNameChanged(theme => this.#editor()
        .setThemeName(theme == PlatformTheme.DARK ? CodeEditorTheme.DARK : CodeEditorTheme.LIGHT))

        return verticalGrid({spacing: 1, wrap: "nowrap"})
        .pushWidget(this.#radios)
        .pushWidget(this.#editor());
    })

    #panel = checkBoxPanel(this.#script(), {label: "Скрипт инициализации", checked: Boolean(this.properties.groovyContent || this.properties.kotlinContent)})
    .onCheck(checked => {
        if (checked) {
            return
        }
        this.lock(() => {
            this.configuration.groovyContent.value = ""
            this.configuration.kotlinContent.value = ""
            this.configuration.format.value = GRADLE_KTS_SCRIPT_FORMAT;
            this.#editor().clearText()
        })
    })

    onChange = (action: DispatchWithoutAction) => {
        this.configuration.change.handle(action);
        return this;
    }

    groovyContent = () => this.configuration.groovyContent.value;

    kotlinContent = () => this.configuration.kotlinContent.value;

    format = () => this.configuration.format.value;

    draw = this.#panel.render
}

export const gradleInitScript = (properties: Properties) => new GradleInitialScript(properties, Configuration)
