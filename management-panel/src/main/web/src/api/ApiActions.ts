import 'moment/locale/ru'
import {API_ERROR, KNOWN_ERROR_CODES, UNAUTHORIZED} from "../constants/ErrorCodes";
import {ServiceExecutionException, ServiceExceptionDispatch} from "../model/ApiTypes";
import {useNotifications} from "../framework/hooks/Hooks";
import {platform} from "../component/entry/EntryPoint";
import {doNothing} from "../framework/constants/Constants";

export const useApiActions = () => {
    const notifications = useNotifications();

    return {
        errorHandler: (onError: ServiceExceptionDispatch = doNothing) => (error: any) => {
            if (!(error instanceof ServiceExecutionException)) {
                notifications.customError(() => notifications.createSingleLineSnack(error));
                onError(new ServiceExecutionException(API_ERROR, error, ""));
                return
            }

            if (error.errorCode == UNAUTHORIZED) {
                platform.logOut();
                return;
            }

            if (!KNOWN_ERROR_CODES.some(code => error.errorCode.startsWith(code))) {
                notifications.customError(!error.errorMessage
                    ? () => notifications.createSingleLineSnack(error.errorCode)
                    : () => notifications.createTwoLineSnack(error.errorCode, error.errorMessage));
            }

            onError(error)
        }
    }
};
