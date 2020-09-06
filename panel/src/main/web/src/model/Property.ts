import {TextProperty} from "./TextProperty";
import {ResourceProperty} from "./ResourceProperty";

export type Property = {
    name: string
    type: string
    textProperty?: TextProperty
    resourceProperty?: ResourceProperty
}
