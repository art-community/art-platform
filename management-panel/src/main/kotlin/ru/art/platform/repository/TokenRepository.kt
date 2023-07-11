package ru.art.platform.repository

import ru.art.core.factory.CollectionsFactory.*
import ru.art.entity.PrimitivesFactory.*
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.tarantool.constants.TarantoolModuleConstants.*
import ru.art.tarantool.dao.TarantoolDao.*
import java.util.*

object TokenRepository {
    fun putToken(token: String): String = tarantool(PLATFORM_CAMEL_CASE).put(TOKEN_CAMEL_CASE, stringPrimitive(token)).getString(VALUE)

    fun getToken(token: String): Optional<String> = tarantool(PLATFORM_CAMEL_CASE)
            .getByIndex(TOKEN_CAMEL_CASE, TOKEN_CAMEL_CASE, setOf(token))
            .map { entity -> entity.getString(VALUE) }
}