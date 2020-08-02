import {useTheme} from "@material-ui/core";
import {smallLoader} from "../../../framework/dsl/simple/SimpleLoader";
import {hooked} from "../../../framework/pattern/Hooked";
import {PlatformTheme} from "../../../constants/PlatformTheme";

export const magicLoader = (labeled?: boolean) => !labeled
    ? smallLoader()
    : hooked(useTheme).cache(theme => {
        const label = `Творится ${theme.palette.type == PlatformTheme.DARK ? "черная" : "белая"} магия...`;
        const color = theme.palette.primary.main;
        return smallLoader(label, color);
    });
