package ru.art.platform.panel.service

import reactor.core.publisher.Flux
import ru.art.config.extensions.ConfigExtensions.configString
import ru.art.platform.api.model.request.UserAuthorizationRequest
import ru.art.platform.api.model.request.UserRegistrationRequest
import ru.art.platform.api.model.user.User
import ru.art.platform.panel.broker.userConsumer
import ru.art.platform.common.broker.PlatformEvent
import ru.art.platform.common.constants.ErrorCodes.*
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.panel.constants.ConfigKeys.USER_ADMINISTRATOR_NAME_KEY
import ru.art.platform.panel.constants.ConfigKeys.USER_ADMINISTRATOR_PASSWORD_KEY
import ru.art.platform.panel.constants.UserActions.DEFAULT_USER_RIGHTS
import ru.art.platform.panel.constants.UserActions.GLOBAL_ADMINISTRATOR_ACTIONS
import ru.art.platform.panel.repository.UserRepository.getUserByEmail
import ru.art.platform.panel.repository.UserRepository.getUserByName
import ru.art.platform.panel.repository.UserRepository.getUserByToken
import ru.art.platform.panel.repository.UserRepository.getUsers
import ru.art.platform.panel.repository.UserRepository.insertUser
import ru.art.platform.panel.repository.UserRepository.putUser
import ru.art.platform.panel.service.TokenService.checkToken
import ru.art.platform.panel.service.TokenService.generateToken

object UserService {
    fun authenticate(token: String): User = if (checkToken(token)) getUserByToken(token)
    else throw PlatformException(UNAUTHORIZED)

    fun authorize(request: UserAuthorizationRequest): User = getUserByName(request.name).or { getUserByEmail(request.name) }
            .map { user ->
                if (String(user.password) != request.password) {
                    throw PlatformException(INVALID_PASSWORD)
                }
                if (!checkToken(user.token)) {
                    return@map putUser(user.toBuilder().token(generateToken(request.name)).build())
                }
                return@map user
            }
            .orElseThrow { PlatformException(USER_DOES_NOT_EXISTS) }

    fun registerUser(request: UserRegistrationRequest): User {
        val user = User.builder()
                .name(request.name)
                .fullName(request.fullName)
                .password(request.password.toByteArray())
                .token(generateToken(request.name))
                .email(request.email)
                .availableActions(getInitialUserActions(request.name, request.password))
                .admin(isAdmin(request.name, request.password))
                .build()
        getUserByName(request.name).ifPresent { throw PlatformException(USER_ALREADY_EXISTS) }
        getUserByEmail(request.email).ifPresent { throw PlatformException(USER_ALREADY_EXISTS) }
        return insertUser(user)
    }

    fun getUserNames(): Set<String> = getUsers().map(User::getName).toSet()

    fun getUserEmails(): Set<String> = getUsers().map(User::getEmail).toSet()

    fun getInitialUserActions(name: String, password: String): Set<String> {
        if (isAdmin(name, password)) return GLOBAL_ADMINISTRATOR_ACTIONS
        return DEFAULT_USER_RIGHTS
    }

    fun subscribeOnUser(): Flux<PlatformEvent> = userConsumer()

    private fun isAdmin(name: String, password: String) = password == configString(USER_ADMINISTRATOR_PASSWORD_KEY) && name == configString(USER_ADMINISTRATOR_NAME_KEY)
}
