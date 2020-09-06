import {cyan, deepPurple, lightGreen, orange, purple, teal, yellow} from "@material-ui/core/colors";
import {createMuiTheme, Theme} from "@material-ui/core";

export enum PlatformTheme {
    DARK = 'dark',
    LIGHT = 'light'
}

export const PRIMARY_MAIN_COLOR_LIGHT = purple['700'];
export const SECONDARY_MAIN_COLOR_LIGHT = orange["800"];

export const PRIMARY_MAIN_COLOR_DARK = teal["200"];
export const SECONDARY_MAIN_COLOR_DARK = lightGreen["200"];

export const LIGHT_THEME = createMuiTheme({
    palette: {
        type: PlatformTheme.LIGHT,
        primary: {main: PRIMARY_MAIN_COLOR_LIGHT},
        secondary: {main: SECONDARY_MAIN_COLOR_LIGHT}
    },
    zIndex: {
        snackbar: 2000
    }
});

export const DARK_THEME = createMuiTheme({
    palette: {
        type: PlatformTheme.DARK,
        primary: {main: PRIMARY_MAIN_COLOR_DARK},
        secondary: {main: SECONDARY_MAIN_COLOR_DARK}
    },
    zIndex: {
        snackbar: 2000
    }
});

export const THEMES = new Map<string, Theme>()
.set(PlatformTheme.DARK, DARK_THEME)
.set(PlatformTheme.LIGHT, LIGHT_THEME);
