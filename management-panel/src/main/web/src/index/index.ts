import * as ReactDOM from "react-dom";
import 'reflect-metadata'
import "../framework/extensions/extensions"
import maintenanceCat from "../images//maintenance-cat.png"
import {disposeRsockets, PlatformClient} from "../client/PlatformClient";
// @ts-ignore
import {registerObserver} from "react-perf-devtool";
import {deactivateStreams} from "../streams/Streams";
import {disableScroll, enableScroll} from "../service/ScrollService";
import {DEFAULT_RSOCKET_CLIENT_NAME} from "../constants/ApiConstants";
import {bigLoader} from "../framework/dsl/simple/SimpleLoader";
import {platform} from "../component/entry/EntryPoint";
import {container} from "../framework/dsl/simple/SimpleContainer";
import {verticalGrid} from "../framework/dsl/managed/ManagedGrid";
import {label} from "../framework/dsl/managed/ManagedLabel";
import {image} from "../framework/dsl/simple/SimpleImage";
import {MAIN_COMPONENT} from "../constants/WidgetsConstants";
import {randomColor} from "../framework/constants/Constants";
import {development} from "../constants/Environment";

console.log(`Environment: ${process.env.NODE_ENV}`);
disableScroll();

if (development()) {
    // @ts-ignore
    window.observer = registerObserver();
}

window.addEventListener("beforeunload", event => {
    event.preventDefault();
    deactivateStreams();
    disposeRsockets()
});

const style = {
    container: {
        minHeight: "100vh"
    },
    label: {
        color: randomColor()
    }
};

const failedWidget =
    container(
        verticalGrid({justify: "center", alignContent: "center", alignItems: "center"})
        .pushWidget(label({variant: "h2", style: style.label, text: "Скоро всё будет хорошо"}))
        .pushWidget(image({height: "800", width: "800", src: maintenanceCat, alt: "Упс :("}))
    ).render;

const succeededWidget = platform.render;

const loadingWidget = bigLoader().render;

const connected = () => {
    enableScroll();
    ReactDOM.render(succeededWidget(), document.getElementById(MAIN_COMPONENT))
};

const failed = () => {
    ReactDOM.render(failedWidget(), document.getElementById(MAIN_COMPONENT));
};

ReactDOM.render(loadingWidget(), document.getElementById(MAIN_COMPONENT));
PlatformClient.platformClient().connect(DEFAULT_RSOCKET_CLIENT_NAME, connected, failed);
