package ru.art.platform.panel.specification

import org.eclipse.jgit.internal.storage.file.FileRepository
import ru.art.entity.PrimitiveMapping.longMapper
import ru.art.entity.PrimitiveMapping.stringMapper
import ru.art.entity.Value
import ru.art.platform.api.mapping.file.PlatformFileChunkMapper.toPlatformFileChunk
import ru.art.platform.api.mapping.file.PlatformFileIdentifierMapper.fromPlatformFileIdentifier
import ru.art.platform.api.mapping.file.PlatformFileMapper.fromPlatformFile
import ru.art.platform.api.mapping.file.PlatformFileMapper.toPlatformFile
import ru.art.platform.api.mapping.request.CloneFileRequestMapper.toCloneFileRequest
import ru.art.platform.panel.constants.ServiceConstants.ALLOCATE_FILE
import ru.art.platform.panel.constants.ServiceConstants.CLONE_FILE
import ru.art.platform.panel.constants.ServiceConstants.GET_FILE
import ru.art.platform.panel.constants.ServiceConstants.PUT_FILE
import ru.art.platform.panel.constants.ServiceConstants.UPLOAD_FILE
import ru.art.platform.panel.repository.FileMetaRepository
import ru.art.platform.panel.service.FileService
import ru.art.reactive.service.constants.ReactiveServiceModuleConstants.ReactiveMethodProcessingMode.REACTIVE
import ru.art.rsocket.function.RsocketServiceFunction.rsocket
import ru.art.service.constants.RequestValidationPolicy.NOT_NULL

fun registerFileService() {
    rsocket(ALLOCATE_FILE)
            .requestMapper(stringMapper.toModel)
            .responseMapper(fromPlatformFileIdentifier)
            .handle(FileMetaRepository::allocateFile)
    rsocket(PUT_FILE)
            .requestMapper(toPlatformFile)
            .responseMapper(fromPlatformFile)
            .handle(FileService::putFile)
    rsocket(CLONE_FILE)
            .requestMapper(toCloneFileRequest)
            .responseMapper(fromPlatformFile)
            .handle(FileService::cloneFile)
    rsocket(UPLOAD_FILE)
            .requestMapper(toPlatformFileChunk)
            .responseMapper<Any> { value -> value as Value }
            .requestProcessingMode(REACTIVE)
            .responseProcessingMode(REACTIVE)
            .handle(FileService::uploadFile)
    rsocket(GET_FILE)
            .validationPolicy(NOT_NULL)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromPlatformFile)
            .handle(FileService::getFile)
}
