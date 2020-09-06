package ru.art.platform.panel.repository

import ru.art.entity.CollectionValuesFactory.*
import ru.art.platform.api.mapping.file.PlatformFileIdentifierMapper.toPlatformFileIdentifier
import ru.art.platform.api.model.file.*
import ru.art.platform.common.constants.ErrorCodes.*
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.platform.common.exception.*
import ru.art.platform.panel.constants.DbConstants.PLATFORM_FILE_DATA_SPACE
import ru.art.tarantool.dao.TarantoolDao.*

object FileDataRepository {
    fun putFileData(id: Long, data: ByteArray): PlatformFileIdentifier = toPlatformFileIdentifier.map(tarantool(PLATFORM_CAMEL_CASE)
            .put(PLATFORM_FILE_DATA_SPACE, id, byteCollection(data)))


    fun getFileData(id: Long): ByteArray =
            tarantool(PLATFORM_CAMEL_CASE)
                    .getCollectionValue(PLATFORM_FILE_DATA_SPACE, id)
                    .map { bytes -> bytes.byteArray }
                    .orElseThrow { PlatformException(FILE_DOES_NOT_EXISTS, "File with id '${id}' does not exists") }

    fun deleteFileData(id: Long) {
        tarantool(PLATFORM_CAMEL_CASE).delete(PLATFORM_FILE_DATA_SPACE, id)
    }
}
