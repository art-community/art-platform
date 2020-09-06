package ru.art.platform.service

import com.auth0.jwt.JWT.require
import com.auth0.jwt.algorithms.Algorithm.HMAC256
import ru.art.config.extensions.ConfigExtensions.configString
import ru.art.core.extension.ExceptionExtensions.ifException
import ru.art.platform.common.constants.PlatformKeywords.PLATFORM_CAMEL_CASE
import ru.art.platform.constants.ConfigKeys.SECRET_KEY
import ru.art.platform.factory.TokenFactory.createToken
import ru.art.platform.repository.TokenRepository.getToken
import ru.art.platform.repository.TokenRepository.putToken

object TokenService {
    fun generateToken(name: String): String = putToken(createToken(name))

    fun checkToken(token: String): Boolean = getToken(token)
            .map {
                ifException({
                    require(HMAC256(configString(SECRET_KEY)))
                            .withIssuer(PLATFORM_CAMEL_CASE)
                            .build()
                            .verify(token)
                    return@ifException true
                }, false)
            }
            .orElse(false)
}
