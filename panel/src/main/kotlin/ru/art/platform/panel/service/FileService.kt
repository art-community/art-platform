package ru.art.platform.panel.service

import io.netty.buffer.ByteBufAllocator.DEFAULT
import reactor.core.publisher.Flux
import reactor.core.publisher.UnicastProcessor
import ru.art.logging.LoggingModule.loggingModule
import ru.art.platform.api.model.file.PlatformFile
import ru.art.platform.api.model.file.PlatformFileChunk
import ru.art.platform.api.model.request.CloneFileRequest
import ru.art.platform.common.constants.ErrorCodes.FILE_DOES_NOT_EXISTS
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.panel.constants.RsocketConstants.FILE_STREAM_CHUNKS_COUNT
import ru.art.platform.panel.extensions.chunked
import ru.art.platform.panel.repository.FileDataRepository.deleteFileData
import ru.art.platform.panel.repository.FileDataRepository.getFileData
import ru.art.platform.panel.repository.FileDataRepository.putFileData
import ru.art.platform.panel.repository.FileMetaRepository.allocateFile
import ru.art.platform.panel.repository.FileMetaRepository.deleteFileMetaData
import ru.art.platform.panel.repository.FileMetaRepository.getFileId
import ru.art.platform.panel.repository.FileMetaRepository.tryGetFileId
import java.util.*
import java.util.concurrent.atomic.AtomicReference

object FileService {
    fun putFile(file: PlatformFile): PlatformFile {
        putFileData(file.id, file.bytes)
        return PlatformFile.builder()
                .id(file.id)
                .name(file.name)
                .bytes(file.bytes)
                .build()
    }

    fun putFile(name: String, bytes: ByteArray): PlatformFile = putFile(PlatformFile.builder()
            .id(allocateFile(name).id)
            .name(name)
            .bytes(bytes)
            .build())

    fun cloneFile(request: CloneFileRequest): PlatformFile = with(request) {
        tryGetFile(currentFileId.id)
                .map { file -> putFile(file.toBuilder().id(request.newFileId.id).build()) }
                .orElseThrow { PlatformException(FILE_DOES_NOT_EXISTS, "File with a id '$currentFileId' does not exist") }
    }

    fun uploadFile(fileSource: Flux<PlatformFileChunk>): Flux<Any> {
        val buffer = DEFAULT.buffer()
        val lastChunk = AtomicReference<PlatformFileChunk>()
        val responder = UnicastProcessor.create<Any>()
        val writeChunk: (PlatformFileChunk) -> Unit = { chunk ->
            lastChunk.set(chunk)
            buffer.writeBytes(chunk.bytes)
        }
        val writeFile = {
            val array = ByteArray(buffer.readableBytes())
            buffer.readBytes(array)
            putFile(PlatformFile.builder()
                    .id(lastChunk.get().id.id)
                    .name(lastChunk.get().id.name)
                    .bytes(array)
                    .build())
            responder.onComplete()
        }
        fileSource.chunked(FILE_STREAM_CHUNKS_COUNT).subscribe(writeChunk, (loggingModule().getLogger(FileService::class.java)::error), writeFile)
        return responder
    }

    fun getFile(id: Long): PlatformFile {
        val fileId = getFileId(id)
        return PlatformFile.builder()
                .id(fileId.id)
                .name(fileId.name)
                .bytes(getFileData(fileId.id))
                .build()
    }

    fun tryGetFile(id: Long): Optional<PlatformFile> = tryGetFileId(id).map { fileId ->
        PlatformFile.builder()
                .id(fileId.id)
                .name(fileId.name)
                .bytes(getFileData(fileId.id))
                .build()
    }

    fun deleteFile(id: Long) {
        deleteFileMetaData(id)
        deleteFileData(id)
    }
}
