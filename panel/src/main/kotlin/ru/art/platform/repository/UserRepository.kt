package ru.art.platform.repository

import ru.art.core.factory.CollectionsFactory.fixedArrayOf
import ru.art.core.factory.CollectionsFactory.setOf
import ru.art.platform.api.mapping.user.UserMapper.fromUser
import ru.art.platform.api.mapping.user.UserMapper.toUser
import ru.art.platform.api.model.user.User
import ru.art.platform.common.constants.ErrorCodes.USER_DOES_NOT_EXISTS
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.constants.DbConstants.EMAIL_INDEX_NAME
import ru.art.platform.service.SecuringService.decrypt
import ru.art.platform.service.SecuringService.encrypt
import ru.art.tarantool.dao.TarantoolDao.tarantool
import java.time.Instant.now
import java.util.*

object UserRepository {
    fun getUser(id: Long): User = tarantool(PLATFORM_CAMEL_CASE)
            .get(USER_CAMEL_CASE, id)
            .map(toUser::map)
            .map { user -> user.toBuilder().password(decrypt(user.password)).build() }
            .orElseThrow { PlatformException(USER_DOES_NOT_EXISTS, "User with an id '$id' does not exist") }

    fun getUserByToken(token: String): User = tarantool(PLATFORM_CAMEL_CASE)
            .getByIndex(USER_CAMEL_CASE, TOKEN_CAMEL_CASE, setOf(token))
            .map(toUser::map)
            .map { user -> user.toBuilder().password(decrypt(user.password)).build() }
            .orElseThrow { PlatformException(USER_DOES_NOT_EXISTS, "User with token '$token' does not exist") }

    fun putUser(user: User): User = toUser.map(tarantool(PLATFORM_CAMEL_CASE).put(USER_CAMEL_CASE, fromUser.map(user.toBuilder()
            .password(encrypt(user.password))
            .updateTimeStamp(now().epochSecond).build())))

    fun insertUser(user: User): User = toUser.map(tarantool(PLATFORM_CAMEL_CASE).insert(USER_CAMEL_CASE, fromUser.map(user.toBuilder()
            .password(encrypt(user.password))
            .build())))

    fun getUserByName(name: String): Optional<User> = tarantool(PLATFORM_CAMEL_CASE)
            .getByIndex(USER_CAMEL_CASE, NAME_CAMEL_CASE, fixedArrayOf(name))
            .map(toUser::map)
            .map { user -> user.toBuilder().password(decrypt(user.password)).build() }

    fun getUserByEmail(email: String): Optional<User> = tarantool(PLATFORM_CAMEL_CASE)
            .getByIndex(USER_CAMEL_CASE, EMAIL_INDEX_NAME, fixedArrayOf(email))
            .map(toUser::map)
            .map { user -> user.toBuilder().password(decrypt(user.password)).build() }

    fun getUsers(): Set<User> = tarantool(PLATFORM_CAMEL_CASE)
            .selectAll(USER_CAMEL_CASE)
            .map(toUser::map)
            .map { user -> user.toBuilder().password(decrypt(user.password)).build() }
            .toSet()

    fun deleteUser(id: Long): User = tarantool(PLATFORM_CAMEL_CASE)
            .delete(USER_CAMEL_CASE, setOf(id))
            .map(toUser::map)
            .map { user -> user.toBuilder().password(decrypt(user.password)).build() }
            .orElseThrow { PlatformException(USER_DOES_NOT_EXISTS, "User with an id '$id' does not exist") }
}
