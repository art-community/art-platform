import * as React from "react";
// @ts-ignore
import Filter from "ansi-to-html/lib/ansi_to_html"
import {isEmptyArray} from "../../../framework/extensions/extensions";
import {smallLoader} from "../../../framework/dsl/simple/SimpleLoader";
import {html} from "../../../framework/widgets/Html";
import {Widget} from "../../../framework/widgets/Widget";
import {AUTO_SCROLL_THRESHOLD} from "../../../constants/WidgetsConstants";
import {conditional} from "../../../framework/pattern/Conditional";
import {Configurable} from "../../../framework/pattern/Configurable";

type Properties = {
    loading: boolean
}

class Configuration extends Configurable<Properties> {
    loading = this.property(this.defaultProperties.loading)

    records = this.property<string[]>([])
}

export class LogViewer extends Widget<LogViewer, Properties, Configuration> {
    #filter = new Filter();

    #scrolledOnLoad = false;

    #scroll = (element?: HTMLDivElement) => {
        if (!element) {
            return;
        }
        const location = element.getBoundingClientRect();
        if (!this.#scrolledOnLoad) {
            element.scrollIntoView();
            this.#scrolledOnLoad = true;
            return;
        }
        const needScroll =
            location.top >= 0 &&
            location.bottom - (location.bottom * AUTO_SCROLL_THRESHOLD) <=
            (window.innerHeight || document.documentElement.clientHeight);

        if (needScroll) {
            element.scrollIntoView();
        }
    };

    #loader = conditional(() => this.configuration.loading.value).persist(smallLoader);

    #record = (line: string, index: number) =>
        <div key={index}>
            {html(this.#filter.toHtml(line)).render()}
        </div>;

    #records = () => {
        if (isEmptyArray(this.configuration.records.value)) {
            return <></>
        }
        const records = this.configuration.records.value!;
        if (records.length == 1) {
            return <div key={0} ref={ref => ref && this.#scroll(ref)}>
                {html(this.#filter.toHtml(records.last())).render()}
            </div>
        }
        return <>
            {records.slice(0, records.length - 2).map(this.#record)}
            <div key={records.length - 1} ref={ref => ref && this.#scroll(ref)}>
                {html(this.#filter.toHtml(records.last())).render()}
            </div>
        </>
    };

    setLoading = (loading: boolean) => {
        this.configuration.loading.value = loading;
        return this;
    }

    setRecords = (records: string[]) => {
        this.configuration.records.value = records;
        return this;
    }

    draw = () =>
        <>
            {this.#records()}
            {this.#loader.render()}
        </>
}

export const logViewer = (properties: Properties) => new LogViewer(properties, Configuration)
