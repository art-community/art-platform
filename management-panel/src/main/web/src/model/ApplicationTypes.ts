import {FilebeatApplication} from "./FilebeatTypes";

export type ApplicationIdentifier = {
    id: number
    name: string
    type: string
}

export type Applications = {
    filebeat: FilebeatApplication[]
}
