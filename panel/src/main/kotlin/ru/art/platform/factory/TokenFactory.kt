package ru.art.platform.factory

import com.auth0.jwt.JWT.*
import com.auth0.jwt.algorithms.Algorithm.*
import ru.art.config.extensions.ConfigExtensions.configString
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.platform.constants.CommonConstants.TOKEN_LIFE_TIME_DAYS
import ru.art.platform.constants.ConfigKeys.SECRET_KEY
import java.time.Duration.*
import java.util.*

object TokenFactory {
    fun createToken(name: String): String = create()
            .withIssuer(PLATFORM_CAMEL_CASE)
            .withClaim(NAME_CAMEL_CASE, name)
            .withExpiresAt(Date(Date().time + ofDays(TOKEN_LIFE_TIME_DAYS.toLong()).toMillis()))
            .sign(HMAC256(configString(SECRET_KEY)))
}
