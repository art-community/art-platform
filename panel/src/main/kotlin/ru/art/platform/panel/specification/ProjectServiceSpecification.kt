package ru.art.platform.panel.specification

import ru.art.entity.CollectionMapping.collectionValueFromModel
import ru.art.entity.PrimitiveMapping.longMapper
import ru.art.platform.api.mapping.assembly.AssembledArtifactMapper.fromAssembledArtifact
import ru.art.platform.api.mapping.project.ProjectMapper.fromProject
import ru.art.platform.api.mapping.request.AssembledProjectArtifactsRequestMapper.toAssembledProjectArtifactsRequest
import ru.art.platform.api.mapping.request.ExternalArtifactsRequestMapper.toExternalArtifactsRequest
import ru.art.platform.api.mapping.request.ProjectRequestMapper.toProjectRequest
import ru.art.platform.api.mapping.request.ProjectUpdateRequestMapper.toProjectUpdateRequest
import ru.art.platform.api.model.project.Project
import ru.art.platform.common.broker.PlatformEventMapper.fromPlatformEvent
import ru.art.platform.panel.constants.ServiceConstants.ADD_EXTERNAL_ARTIFACTS
import ru.art.platform.panel.constants.ServiceConstants.ADD_PROJECT
import ru.art.platform.panel.constants.ServiceConstants.DELETE_PROJECT
import ru.art.platform.panel.constants.ServiceConstants.GET_ASSEMBLED_PROJECT_ARTIFACTS
import ru.art.platform.panel.constants.ServiceConstants.GET_INITIALIZED_PROJECTS
import ru.art.platform.panel.constants.ServiceConstants.GET_PROJECT
import ru.art.platform.panel.constants.ServiceConstants.GET_PROJECTS
import ru.art.platform.panel.constants.ServiceConstants.RELOAD_PROJECT
import ru.art.platform.panel.constants.ServiceConstants.SUBSCRIBE_ON_PROJECT
import ru.art.platform.panel.constants.ServiceConstants.UPDATE_PROJECT
import ru.art.platform.panel.repository.ProjectRepository
import ru.art.platform.panel.service.ProjectService
import ru.art.reactive.service.constants.ReactiveServiceModuleConstants.ReactiveMethodProcessingMode.REACTIVE
import ru.art.rsocket.function.RsocketServiceFunction.rsocket
import ru.art.service.constants.RequestValidationPolicy.NOT_NULL
import ru.art.service.constants.RequestValidationPolicy.VALIDATABLE

fun registerProjectService() {
    rsocket(ADD_PROJECT)
            .requestMapper(toProjectRequest)
            .validationPolicy(VALIDATABLE)
            .responseMapper(fromProject)
            .handle(ProjectService::addProject)
    rsocket(RELOAD_PROJECT)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromProject)
            .validationPolicy(NOT_NULL)
            .handle(ProjectService::reloadProject)
    rsocket(UPDATE_PROJECT)
            .requestMapper(toProjectUpdateRequest)
            .validationPolicy(VALIDATABLE)
            .responseMapper(fromProject)
            .handle(ProjectService::updateProject)
    rsocket(GET_PROJECTS)
            .responseMapper(collectionValueFromModel(fromProject)::map)
            .produce(ProjectRepository::getProjects)
    rsocket(GET_INITIALIZED_PROJECTS)
            .responseMapper(collectionValueFromModel(fromProject)::map)
            .produce(ProjectRepository::getInitializedProjects)
    rsocket(GET_ASSEMBLED_PROJECT_ARTIFACTS)
            .validationPolicy(VALIDATABLE)
            .requestMapper(toAssembledProjectArtifactsRequest)
            .responseMapper(collectionValueFromModel(fromAssembledArtifact)::map)
            .handle(ProjectService::getAssembledProjectArtifacts)
    rsocket(GET_PROJECT)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .responseMapper(fromProject::map)
            .handle<Long, Project>(ProjectRepository::getProject)
    rsocket(DELETE_PROJECT)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .responseMapper(fromProject)
            .handle(ProjectService::deleteProject)
    rsocket(ADD_EXTERNAL_ARTIFACTS)
            .requestMapper(toExternalArtifactsRequest)
            .validationPolicy(VALIDATABLE)
            .handle(ProjectService::addExternalArtifacts)
    rsocket(SUBSCRIBE_ON_PROJECT)
            .responseMapper(fromPlatformEvent)
            .responseProcessingMode(REACTIVE)
            .produce(ProjectService::subscribeOnProject)
}
