import {horizontalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {useTheme} from "@material-ui/core";
import {observe} from "../../../framework/pattern/Observable";
import {TECHNOLOGIES} from "../../../constants/TechnologyConstants";
import {image} from "../../../framework/dsl/simple/SimpleImage";
import {tooltip} from "../../../framework/dsl/simple/SimpleTooltip";
import {link} from "../../../framework/dsl/simple/SimpleLink";
import {hooked} from "../../../framework/pattern/Hooked";

const useStyle = (width: number, height: number) => {
    const theme = useTheme();
    return observe(theme, width, height).render(() => ({
        technologyAvatar: {
            width: width,
            height: height,
            boxShadow: `0 0 7px ${theme.palette.primary.main}`,
            padding: 7,
            margin: 7,
            border: `1px solid ${theme.palette.primary.main}`
        },
    }));
};

export const technologyIcon = (technology: string, enableTooltip?: boolean) => {
    const technologyData = TECHNOLOGIES.find(current => current.name == technology)!;

    return hooked(() => useStyle(technologyData.width, technologyData.height))
    .cache(style => {
        const iconImage = image({
            style: style.technologyAvatar,
            width: technologyData.width,
            height: technologyData.height,
            src: technologyData.icon
        });

        const iconTooltip = tooltip({interactive: true})
        .widget(iconImage)
        .title(link({
            label: technologyData.descriptionLabel,
            reference: technologyData.descriptionReference,
            description: technologyData.description
        }));

        return !enableTooltip ? iconImage : iconTooltip;
    });
};

export const labeledTechnologyIcon = (technology: string, iconLabel: string, enableTooltip?: boolean) => {
    const technologyData = TECHNOLOGIES.find(current => current.name == technology)!;
    return hooked(() => useStyle(technologyData.width, technologyData.height))
    .cache(style => {
        const iconImage = image({
            style: style.technologyAvatar,
            width: technologyData.width,
            height: technologyData.height,
            src: technologyData.icon
        });

        const iconTooltip = tooltip({interactive: true})
        .widget(iconImage)
        .title(link({
            label: technologyData.descriptionLabel,
            reference: technologyData.descriptionReference,
            description: technologyData.description
        }));

        return horizontalGrid({alignItems: "center", wrap: "nowrap", spacing: 1})
        .pushWidget(!enableTooltip ? iconImage : iconTooltip)
        .pushWidget(label({color: "secondary", text: iconLabel}));
    });
};

export const technologyIcons = (technologies: string[], enableTooltip?: boolean, iconLabel?: string) => iconLabel
    ? horizontalGrid({
        wrap: "nowrap",
        spacing: 1,
        alignItems: "center"
    })
    .pushWidget(label({noWrap: true, color: "secondary", text: `${iconLabel}:`}))
    .pushWidget(horizontalGrid({spacing: 1})
        .pushWidgets(technologies.map(technology => technologyIcon(technology, enableTooltip)))
    )

    : horizontalGrid({spacing: 1}).pushWidgets(technologies.map(technology => technologyIcon(technology, enableTooltip)));
