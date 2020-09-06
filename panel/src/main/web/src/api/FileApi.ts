import {chunkedRequest, createFunctionRequest, requestResponse} from "../client/PlatformClient";
import {ALLOCATE_FILE, CLONE_FILE, GET_FILE, UPLOAD_FILE} from "../constants/ApiConstants";
import {Dispatch, DispatchWithoutAction} from "react";
import {useApiActions} from "./ApiActions";
import {FILE_CHUNK_SIZE} from "../constants/FileConstants";
import {PlatformFile, PlatformFileCloneRequest, PlatformFileIdentifier, PlatformFilePayload} from "../model/PlatformFileTypes";

export const useFileApi = () => {
    const actions = useApiActions();

    return {
        uploadFile: (payload: PlatformFilePayload, onComplete: DispatchWithoutAction) => {
            const fileSource = Array.from(payload.bytes)
            .chunks(FILE_CHUNK_SIZE)
            .map(bytes => createFunctionRequest(UPLOAD_FILE, ({
                id: payload.id,
                bytes: bytes,
                size: payload.bytes.length
            })));

            return chunkedRequest(fileSource, onComplete);
        },
        cloneFile: (request: PlatformFileCloneRequest, onComplete: DispatchWithoutAction) => {
            requestResponse(createFunctionRequest(CLONE_FILE, request), onComplete, actions.errorHandler());
        },
        allocateFile: (request: string, onComplete: Dispatch<PlatformFileIdentifier>) => {
            requestResponse(createFunctionRequest(ALLOCATE_FILE, request), onComplete, actions.errorHandler());
        },
        getFile: (request: number, onComplete: Dispatch<PlatformFile>) => {
            requestResponse(createFunctionRequest(GET_FILE, request), onComplete, actions.errorHandler());
        }
    };
};
