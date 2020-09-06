package ru.art.platform.panel.specification

import ru.art.entity.CollectionMapping.collectionValueFromModel
import ru.art.entity.PrimitiveMapping.longMapper
import ru.art.entity.PrimitiveMapping.stringMapper
import ru.art.platform.api.mapping.request.UserAuthorizationRequestMapper.toUserAuthorizationRequest
import ru.art.platform.api.mapping.request.UserRegistrationRequestMapper.toUserRegistrationRequest
import ru.art.platform.api.mapping.user.UserMapper.fromUser
import ru.art.platform.api.mapping.user.UserMapper.toUser
import ru.art.platform.common.broker.PlatformEventMapper.fromPlatformEvent
import ru.art.platform.panel.constants.ServiceConstants.AUTHENTICATE
import ru.art.platform.panel.constants.ServiceConstants.AUTHORIZE
import ru.art.platform.panel.constants.ServiceConstants.DELETE_USER
import ru.art.platform.panel.constants.ServiceConstants.GET_USER
import ru.art.platform.panel.constants.ServiceConstants.GET_USERS
import ru.art.platform.panel.constants.ServiceConstants.GET_USER_EMAILS
import ru.art.platform.panel.constants.ServiceConstants.GET_USER_NAMES
import ru.art.platform.panel.constants.ServiceConstants.REGISTER_USER
import ru.art.platform.panel.constants.ServiceConstants.SUBSCRIBE_ON_USER
import ru.art.platform.panel.constants.ServiceConstants.UPDATE_USER
import ru.art.platform.panel.repository.UserRepository
import ru.art.platform.panel.service.UserService
import ru.art.reactive.service.constants.ReactiveServiceModuleConstants.ReactiveMethodProcessingMode.REACTIVE
import ru.art.rsocket.function.RsocketServiceFunction.rsocket
import ru.art.service.constants.RequestValidationPolicy.NOT_NULL
import ru.art.service.constants.RequestValidationPolicy.VALIDATABLE

fun registerUserService() {
    rsocket(AUTHENTICATE)
            .requestMapper(stringMapper.toModel)
            .responseMapper(fromUser)
            .validationPolicy(NOT_NULL)
            .handle(UserService::authenticate)
    rsocket(AUTHORIZE)
            .requestMapper(toUserAuthorizationRequest)
            .responseMapper(fromUser)
            .validationPolicy(VALIDATABLE)
            .handle(UserService::authorize)
    rsocket(REGISTER_USER)
            .requestMapper(toUserRegistrationRequest)
            .responseMapper(fromUser)
            .validationPolicy(VALIDATABLE)
            .handle(UserService::registerUser)
    rsocket(GET_USER)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .responseMapper(fromUser)
            .handle(UserRepository::getUser)
    rsocket(GET_USERS)
            .responseMapper(collectionValueFromModel(fromUser)::map)
            .produce(UserRepository::getUsers)
    rsocket(GET_USER_NAMES)
            .responseMapper(collectionValueFromModel(stringMapper.fromModel)::map)
            .produce(UserService::getUserNames)
    rsocket(GET_USER_EMAILS)
            .responseMapper(collectionValueFromModel(stringMapper.fromModel)::map)
            .produce(UserService::getUserEmails)
    rsocket(UPDATE_USER)
            .requestMapper(toUser)
            .responseMapper(fromUser)
            .handle(UserRepository::putUser)
    rsocket(DELETE_USER)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromUser)
            .handle(UserRepository::deleteUser)
    rsocket(SUBSCRIBE_ON_USER)
            .responseMapper(fromPlatformEvent)
            .responseProcessingMode(REACTIVE)
            .produce(UserService::subscribeOnUser)

}
