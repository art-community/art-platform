import {UTF_8} from "../framework/constants/Constants";

export const decoder = () => new TextDecoder(UTF_8)
export const encoder = () => new TextEncoder()
