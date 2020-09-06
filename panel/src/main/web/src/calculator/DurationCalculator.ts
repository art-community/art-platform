import moment from "moment";

export const calculateDuration = (endTimeStamp: number, startTimeStamp: number) => {
    const difference = endTimeStamp - startTimeStamp;
    const asSeconds = moment.duration(difference, "seconds").asSeconds();
    const asMinutes = moment.duration(difference, "seconds").asMinutes();
    const asHours = moment.duration(difference, "seconds").asHours();
    if (asHours > 1) {
        return `${asHours.toFixed().toString()} ч`
    }
    if (asMinutes > 1) {
        return `${asMinutes.toFixed().toString()} мин`
    }
    if (asSeconds > 1) {
        return `${asSeconds.toFixed().toString()} сек`
    }
    return `~ 1 сек`;
};
