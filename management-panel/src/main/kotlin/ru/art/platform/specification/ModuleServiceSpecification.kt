package ru.art.platform.specification

import ru.art.entity.CollectionMapping.collectionValueFromModel
import ru.art.entity.PrimitiveMapping.longMapper
import ru.art.platform.api.mapping.module.ModuleFilterCriteriaMapper.toModuleFilterCriteria
import ru.art.platform.api.mapping.module.ModuleInformationMapper.fromModuleInformation
import ru.art.platform.api.mapping.module.ModuleMapper.fromModule
import ru.art.platform.api.mapping.module.ModuleMapper.toModule
import ru.art.platform.api.mapping.request.ModuleInstallationRequestMapper.toModuleInstallationRequest
import ru.art.platform.api.mapping.request.ModuleUpdateRequestMapper.toModuleUpdateRequest
import ru.art.platform.api.mapping.request.UpdateModulesVersionRequestMapper.toUpdateModulesVersionRequest
import ru.art.platform.api.model.module.Module
import ru.art.platform.common.broker.PlatformEventMapper.fromPlatformEvent
import ru.art.platform.constants.ServiceConstants.DELETE_MODULE
import ru.art.platform.constants.ServiceConstants.DELETE_MODULE_FROM_RESOURCE
import ru.art.platform.constants.ServiceConstants.GET_FILTERED_MODULES
import ru.art.platform.constants.ServiceConstants.GET_MODULE
import ru.art.platform.constants.ServiceConstants.GET_MODULES
import ru.art.platform.constants.ServiceConstants.GET_PROJECT_MODULES
import ru.art.platform.constants.ServiceConstants.PROCESS_MODULE_INSTALLATION
import ru.art.platform.constants.ServiceConstants.PROCESS_MODULE_UPDATING
import ru.art.platform.constants.ServiceConstants.REFRESH_MODULE_ARTIFACT
import ru.art.platform.constants.ServiceConstants.REINSTALL_MODULE
import ru.art.platform.constants.ServiceConstants.RESTART_MODULE
import ru.art.platform.constants.ServiceConstants.START_MODULE_INSTALLATION
import ru.art.platform.constants.ServiceConstants.START_MODULE_UPDATING
import ru.art.platform.constants.ServiceConstants.STOP_MODULE
import ru.art.platform.constants.ServiceConstants.SUBSCRIBE_ON_MODULE
import ru.art.platform.constants.ServiceConstants.UPDATE_MODULES_VERSION
import ru.art.platform.repository.ModuleRepository
import ru.art.platform.service.ModuleService
import ru.art.reactive.service.constants.ReactiveServiceModuleConstants.ReactiveMethodProcessingMode.REACTIVE
import ru.art.rsocket.function.RsocketServiceFunction.rsocket
import ru.art.service.constants.RequestValidationPolicy.NOT_NULL
import ru.art.service.constants.RequestValidationPolicy.VALIDATABLE

fun registerModuleService() {
    rsocket(START_MODULE_INSTALLATION)
            .requestMapper(toModuleInstallationRequest)
            .responseMapper(fromModule)
            .validationPolicy(VALIDATABLE)
            .handle(ModuleService::startModuleInstallation)
    rsocket(PROCESS_MODULE_INSTALLATION)
            .requestMapper(toModule)
            .validationPolicy(NOT_NULL)
            .handle(ModuleService::processModuleInstallation)
    rsocket(REINSTALL_MODULE)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .handle(ModuleService::reinstallModule)
    rsocket(START_MODULE_UPDATING)
            .requestMapper(toModuleUpdateRequest)
            .responseMapper(fromModule)
            .validationPolicy(VALIDATABLE)
            .handle(ModuleService::startModuleUpdating)
    rsocket(PROCESS_MODULE_UPDATING)
            .requestMapper(toModule)
            .validationPolicy(NOT_NULL)
            .handle(ModuleService::processModuleUpdating)
    rsocket(UPDATE_MODULES_VERSION)
            .requestMapper(toUpdateModulesVersionRequest)
            .validationPolicy(NOT_NULL)
            .handle(ModuleService::updateModulesVersion)
    rsocket(REFRESH_MODULE_ARTIFACT)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .handle(ModuleService::refreshModuleArtifact)
    rsocket(STOP_MODULE)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .handle(ModuleService::stopModule)
    rsocket(RESTART_MODULE)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .handle(ModuleService::restartModule)
    rsocket(DELETE_MODULE_FROM_RESOURCE)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .handle(ModuleService::deleteModuleFromResource)
    rsocket(GET_MODULE)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromModule)
            .validationPolicy(NOT_NULL)
            .handle(ModuleRepository::getModule)
    rsocket(GET_PROJECT_MODULES)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .responseMapper(collectionValueFromModel(fromModule)::map)
            .handle<Long, List<Module>>(ModuleRepository::getProjectModules)
    rsocket(GET_MODULES)
            .responseMapper(collectionValueFromModel(fromModule)::map)
            .produce(ModuleRepository::getModules)
    rsocket(DELETE_MODULE)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromModule)
            .validationPolicy(NOT_NULL)
            .handle(ModuleService::deleteModule)
    rsocket(SUBSCRIBE_ON_MODULE)
            .responseMapper(fromPlatformEvent)
            .responseProcessingMode(REACTIVE)
            .produce(ModuleService::subscribeOnModule)
    rsocket(GET_FILTERED_MODULES)
            .requestMapper(toModuleFilterCriteria)
            .responseMapper(collectionValueFromModel(fromModuleInformation)::map)
            .handle(ModuleService::getFilteredModules)
}
