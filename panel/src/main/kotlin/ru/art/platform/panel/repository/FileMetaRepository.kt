package ru.art.platform.panel.repository

import ru.art.platform.api.mapping.file.PlatformFileIdentifierMapper.fromPlatformFileIdentifier
import ru.art.platform.api.mapping.file.PlatformFileIdentifierMapper.toPlatformFileIdentifier
import ru.art.platform.api.model.file.PlatformFileIdentifier
import ru.art.platform.common.constants.ErrorCodes.FILE_DOES_NOT_EXISTS
import ru.art.platform.common.constants.PlatformKeywords.PLATFORM_CAMEL_CASE
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.panel.constants.DbConstants.PLATFORM_FILE_META_DATA_SPACE
import ru.art.tarantool.dao.TarantoolDao.tarantool
import java.util.*

object FileMetaRepository {
    fun allocateFile(name: String): PlatformFileIdentifier =
            toPlatformFileIdentifier.map(tarantool(PLATFORM_CAMEL_CASE)
                    .put(PLATFORM_FILE_META_DATA_SPACE, fromPlatformFileIdentifier.map(PlatformFileIdentifier.builder().name(name).build())))

    fun getFileId(id: Long): PlatformFileIdentifier = tryGetFileId(id)
            .orElseThrow { PlatformException(FILE_DOES_NOT_EXISTS, "File with id '${id}' does not exists") }

    fun tryGetFileId(id: Long): Optional<PlatformFileIdentifier> = tarantool(PLATFORM_CAMEL_CASE)
            .get(PLATFORM_FILE_META_DATA_SPACE, id)
            .map(toPlatformFileIdentifier::map)

    fun deleteFileMetaData(id: Long): Optional<PlatformFileIdentifier> = tarantool(PLATFORM_CAMEL_CASE)
            .delete(PLATFORM_FILE_META_DATA_SPACE, id)
            .map(toPlatformFileIdentifier::map)
}
