package ru.art.platform.specification

import ru.art.entity.CollectionMapping.*
import ru.art.entity.PrimitiveMapping.*
import ru.art.platform.api.mapping.assembly.*
import ru.art.platform.api.mapping.assembly.AssembledArtifactMapper.*
import ru.art.platform.api.mapping.assembly.AssemblyConfigurationMapper.*
import ru.art.platform.api.mapping.assembly.AssemblyFilterCriteriaMapper.*
import ru.art.platform.api.mapping.assembly.AssemblyInformationMapper.*
import ru.art.platform.api.mapping.assembly.AssemblyMapper.*
import ru.art.platform.api.mapping.request.BuildRequestMapper.*
import ru.art.platform.common.broker.PlatformEventMapper.fromPlatformEvent
import ru.art.platform.constants.ServiceConstants.BUILD_PROJECT
import ru.art.platform.constants.ServiceConstants.CANCEL_ASSEMBLY
import ru.art.platform.constants.ServiceConstants.DELETE_ASSEMBLY
import ru.art.platform.constants.ServiceConstants.GET_ASSEMBLIES
import ru.art.platform.constants.ServiceConstants.GET_ASSEMBLY
import ru.art.platform.constants.ServiceConstants.GET_ASSEMBLY_CONFIGURATION
import ru.art.platform.constants.ServiceConstants.GET_FILTERED_ASSEMBLIES
import ru.art.platform.constants.ServiceConstants.GET_LATEST_ASSEMBLED_ARTIFACTS
import ru.art.platform.constants.ServiceConstants.REBUILD_PROJECT
import ru.art.platform.constants.ServiceConstants.SAVE_ASSEMBLY_CONFIGURATION
import ru.art.platform.constants.ServiceConstants.SUBSCRIBE_ON_ASSEMBLY
import ru.art.platform.repository.*
import ru.art.platform.service.*
import ru.art.reactive.service.constants.ReactiveServiceModuleConstants.ReactiveMethodProcessingMode.*
import ru.art.rsocket.function.RsocketServiceFunction.*
import ru.art.service.constants.RequestValidationPolicy.*

fun registerAssemblyService() {
    rsocket(GET_LATEST_ASSEMBLED_ARTIFACTS)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .responseMapper(fromAssembly)
            .responseMapper(collectionValueFromModel(fromAssembledArtifact)::map)
            .handle(AssemblyService::getLatestAssembledArtifacts)
    rsocket(BUILD_PROJECT)
            .requestMapper(toBuildRequest)
            .validationPolicy(VALIDATABLE)
            .responseMapper(fromAssembly)
            .handle(AssemblyService::buildProject)
    rsocket(REBUILD_PROJECT)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .consume(AssemblyService::rebuildProject)
    rsocket(GET_ASSEMBLY)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromAssembly::map)
            .handle(AssemblyRepository::getAssembly)
    rsocket(CANCEL_ASSEMBLY)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromAssembly::map)
            .handle(AssemblyService::cancelAssembly)
    rsocket(DELETE_ASSEMBLY)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromAssembly)
            .validationPolicy(NOT_NULL)
            .handle(AssemblyService::deleteAssembly)
    rsocket(GET_ASSEMBLIES)
            .responseMapper(collectionValueFromModel(fromAssembly)::map)
            .produce(AssemblyRepository::getAssemblies)
    rsocket(SUBSCRIBE_ON_ASSEMBLY)
            .responseMapper(fromPlatformEvent)
            .responseProcessingMode(REACTIVE)
            .produce(AssemblyService::subscribeOnAssembly)
    rsocket(SAVE_ASSEMBLY_CONFIGURATION)
            .requestMapper(toAssemblyConfiguration)
            .responseMapper(fromAssemblyConfiguration)
            .validationPolicy(VALIDATABLE)
            .handle(AssemblyConfigurationRepository::saveAssemblyConfiguration)
    rsocket(GET_ASSEMBLY_CONFIGURATION)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromAssemblyConfiguration)
            .handle(AssemblyConfigurationRepository::getAssemblyConfiguration)
    rsocket(GET_FILTERED_ASSEMBLIES)
            .requestMapper(toAssemblyFilterCriteria)
            .responseMapper(collectionValueFromModel(fromAssemblyInformation)::map)
            .handle(AssemblyService::getFilteredAssemblies)
}