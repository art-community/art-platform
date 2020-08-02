// language=RegExp
export const VARIABLE_NAME_REGEX = new RegExp('^[a-zA-Z0-9\\-‌​_]+$');

// language=RegExp
export const LABEL_REGEX = new RegExp('^[a-zA-Zа-яА-Я0-9\\-‌​.$#_=]+$');

// language=RegExp
export const VARIABLE_VALUE_REGEX = new RegExp('^[a-zA-Z0-9\\-‌​.?,\'\\/\\\\;%$#_]+$');

// language=RegExp
export const ENTITY_NAME_REGEX = new RegExp('^[0-9a-z-.]{3,}$');

// language=RegExp
export const USER_NAME_REGEX = new RegExp('^[0-9a-zA-Z-._@]{3,}$');

// language=RegExp
export const FULL_NAME_REGEX = new RegExp('^.{3,}$');

// language=RegExp
export const PASSWORD_REGEX = new RegExp('^[^ ]{3,}$');

// language=RegExp
export const IP_REGEX = new RegExp("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

// language=RegExp
export const HOST_NAME_REGEX = new RegExp("^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$");

// language=RegExp
export const URL_REGEX = new RegExp("(ftp|http|https):\\/\\/(\\w+:?\\w*@)?(\\S+)(:[0-9]+)?(\\/|\\/([\\w#!:.?+=&%@\\-\\/]))?$");

// language=RegExp
export const GIT_URL_REGEX = new RegExp(/((git|ssh|http(s)?)|(git@[\w.]+))(:(\/\/)?)([\w.@:/\-~]+)(\.git)(\/)?/);

// language=RegExp
export const URL_WITHOUT_SCHEME_REGEX = new RegExp("^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$");

// language=RegExp
export const EMAIL_REGEX = new RegExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/);

// language=RegExp
export const CONFIG_FILE_NAME_REGEX = (formats: string[]) => new RegExp(`^[\\w-]+\\.(${formats.reduce((previousValue, currentValue) => previousValue + "|" + currentValue)})$`);

// language=RegExp
export const PORT_REGEX = new RegExp('^([1-9]|[1-5]?[0-9]{2,4}|6[1-4][0-9]{3}|65[1-4][0-9]{2}|655[1-2][0-9]|6553[1-5])$');

// language=RegExp
export const TIMEOUT_REGEX = new RegExp('^[1-9][0-9]*$');

// language=RegExp
export const OPEN_SHIFT_LABEL_REGEX = new RegExp('^[a-z0-9\\-‌​.$#_=]+$');

// language=RegExp
export const DOCKER_IMAGE_REGEX = new RegExp('^[a-zA-Z0-9/:._\\-]+$');

// language=RegExp
export const HOST_NAME_AND_IP_REGEX = new RegExp('^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$|^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)+([A-Za-z]|[A-Za-z][A-Za-z0-9\\-]*[A-Za-z0-9])$');

// language=RegExp
export const POSITIVE_NUMBER_REGEX = new RegExp('^[1-9]+[0-9]*$');
