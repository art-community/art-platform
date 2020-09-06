package ru.art.platform.repository

import ru.art.entity.Entity.entityBuilder
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.tarantool.dao.TarantoolDao.tarantool

object MigrationRepository {
    fun putMigrationStatus(name: String, status: String) {
        tarantool(PLATFORM_CAMEL_CASE)
                .put(MIGRATION_CAMEL_CASE, entityBuilder().stringField("name", name).stringField("status", status).build())
    }

    fun getMigrationStatus(name: String) = tarantool(PLATFORM_CAMEL_CASE)
            .getByIndex(MIGRATION_CAMEL_CASE, NAME_CAMEL_CASE, setOf(name))
            .map { entity -> entity.getString("status") }
}