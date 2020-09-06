package ru.art.platform.panel.specification

import ru.art.entity.CollectionMapping.*
import ru.art.entity.PrimitiveMapping.*
import ru.art.platform.api.mapping.request.ArtifactsResourceRequestMapper.*
import ru.art.platform.api.mapping.request.GitResourceRequestMapper.*
import ru.art.platform.api.mapping.request.OpenShiftResourceRequestMapper.*
import ru.art.platform.api.mapping.request.ProxyResourceRequestMapper
import ru.art.platform.api.mapping.request.ProxyResourceRequestMapper.fromProxyResourceRequest
import ru.art.platform.api.mapping.request.ProxyResourceRequestMapper.toProxyResourceRequest
import ru.art.platform.api.mapping.resource.ArtifactsResourceMapper.fromArtifactsResource
import ru.art.platform.api.mapping.resource.ArtifactsResourceMapper.toArtifactsResource
import ru.art.platform.api.mapping.resource.GitResourceMapper.*
import ru.art.platform.api.mapping.resource.OpenShiftResourceMapper.*
import ru.art.platform.api.mapping.resource.PlatformResourceMapper
import ru.art.platform.api.mapping.resource.PlatformResourceMapper.fromPlatformResource
import ru.art.platform.api.mapping.resource.ProxyResourceMapper.fromProxyResource
import ru.art.platform.api.mapping.resource.ProxyResourceMapper.toProxyResource
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper.*
import ru.art.platform.api.model.resource.*
import ru.art.platform.panel.constants.ServiceConstants.ADD_ARTIFACTS_RESOURCE
import ru.art.platform.panel.constants.ServiceConstants.ADD_GIT_RESOURCE
import ru.art.platform.panel.constants.ServiceConstants.ADD_OPEN_SHIFT_RESOURCE
import ru.art.platform.panel.constants.ServiceConstants.ADD_PROXY_RESOURCE
import ru.art.platform.panel.constants.ServiceConstants.DELETE_ARTIFACTS_RESOURCE
import ru.art.platform.panel.constants.ServiceConstants.DELETE_GIT_RESOURCE
import ru.art.platform.panel.constants.ServiceConstants.DELETE_OPEN_SHIFT_RESOURCE
import ru.art.platform.panel.constants.ServiceConstants.DELETE_PROXY_RESOURCE
import ru.art.platform.panel.constants.ServiceConstants.GET_ARTIFACTS_RESOURCE
import ru.art.platform.panel.constants.ServiceConstants.GET_ARTIFACTS_RESOURCES
import ru.art.platform.panel.constants.ServiceConstants.GET_GIT_RESOURCE
import ru.art.platform.panel.constants.ServiceConstants.GET_GIT_RESOURCES
import ru.art.platform.panel.constants.ServiceConstants.GET_OPEN_SHIFT_RESOURCE
import ru.art.platform.panel.constants.ServiceConstants.GET_OPEN_SHIFT_RESOURCES
import ru.art.platform.panel.constants.ServiceConstants.GET_PLATFORM_RESOURCES
import ru.art.platform.panel.constants.ServiceConstants.GET_PROXY_RESOURCE
import ru.art.platform.panel.constants.ServiceConstants.GET_PROXY_RESOURCES
import ru.art.platform.panel.constants.ServiceConstants.GET_RESOURCE_IDS
import ru.art.platform.panel.constants.ServiceConstants.UPDATE_ARTIFACTS_RESOURCE
import ru.art.platform.panel.constants.ServiceConstants.UPDATE_GIT_RESOURCE
import ru.art.platform.panel.constants.ServiceConstants.UPDATE_OPEN_SHIFT_RESOURCE
import ru.art.platform.panel.constants.ServiceConstants.UPDATE_PROXY_RESOURCE
import ru.art.platform.panel.repository.*
import ru.art.platform.panel.service.*
import ru.art.rsocket.function.RsocketServiceFunction.*
import ru.art.service.constants.RequestValidationPolicy.*

fun registerResourceService() {
    rsocket(ADD_OPEN_SHIFT_RESOURCE)
            .requestMapper(toOpenShiftResourceRequest)
            .validationPolicy(VALIDATABLE)
            .responseMapper(fromOpenShiftResource)
            .handle(ResourceService::addOpenShiftResource)
    rsocket(ADD_ARTIFACTS_RESOURCE)
            .requestMapper(toArtifactsResourceRequest)
            .validationPolicy(VALIDATABLE)
            .responseMapper(fromArtifactsResource)
            .handle(ResourceService::addArtifactsResource)
    rsocket(ADD_GIT_RESOURCE)
            .requestMapper(toGitResourceRequest)
            .validationPolicy(VALIDATABLE)
            .responseMapper(fromGitResource)
            .handle(ResourceService::addGitResource)
    rsocket(ADD_PROXY_RESOURCE)
            .requestMapper(toProxyResourceRequest)
            .validationPolicy(VALIDATABLE)
            .responseMapper(fromProxyResource)
            .handle(ResourceService::addProxyResource)
    rsocket(UPDATE_OPEN_SHIFT_RESOURCE)
            .requestMapper(toOpenShiftResource)
            .validationPolicy(VALIDATABLE)
            .responseMapper(fromOpenShiftResource)
            .handle(ResourceService::updateOpenShiftResource)
    rsocket(UPDATE_ARTIFACTS_RESOURCE)
            .requestMapper(toArtifactsResource)
            .validationPolicy(VALIDATABLE)
            .responseMapper(fromArtifactsResource)
            .handle(ResourceService::updateArtifactsResource)
    rsocket(UPDATE_GIT_RESOURCE)
            .requestMapper(toGitResource)
            .validationPolicy(VALIDATABLE)
            .responseMapper(fromGitResource)
            .handle(ResourceService::updateGitResource)
    rsocket(UPDATE_PROXY_RESOURCE)
            .requestMapper(toProxyResource)
            .validationPolicy(VALIDATABLE)
            .responseMapper(fromProxyResource)
            .handle(ResourceService::updateProxyResource)
    rsocket(GET_OPEN_SHIFT_RESOURCE)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromOpenShiftResource)
            .handle<Long, OpenShiftResource>(OpenShiftResourceRepository::getOpenShiftResource)
    rsocket(GET_OPEN_SHIFT_RESOURCES)
            .responseMapper(collectionValueFromModel(fromOpenShiftResource)::map)
            .produce(OpenShiftResourceRepository::getOpenShiftResources)
    rsocket(GET_PLATFORM_RESOURCES)
            .responseMapper(collectionValueFromModel(fromPlatformResource)::map)
            .produce(PlatformResourceRepository::getPlatformResources)
    rsocket(GET_ARTIFACTS_RESOURCE)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromArtifactsResource)
            .handle<Long, ArtifactsResource>(ArtifactsResourceRepository::getArtifactsResource)
    rsocket(GET_ARTIFACTS_RESOURCES)
            .responseMapper(collectionValueFromModel(fromArtifactsResource)::map)
            .produce(ArtifactsResourceRepository::getArtifactsResources)
    rsocket(GET_RESOURCE_IDS)
            .responseMapper(collectionValueFromModel(fromResourceIdentifier)::map)
            .produce(ResourceService::getResourceIds)
    rsocket(GET_GIT_RESOURCE)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromGitResource)
            .handle<Long, GitResource>(GitResourceRepository::getGitResource)
    rsocket(GET_GIT_RESOURCES)
            .responseMapper(collectionValueFromModel(fromGitResource)::map)
            .produce(GitResourceRepository::getGitResources)
    rsocket(GET_PROXY_RESOURCE)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromProxyResource)
            .handle<Long, ProxyResource>(ProxyResourceRepository::getProxyResource)
    rsocket(GET_PROXY_RESOURCES)
            .responseMapper(collectionValueFromModel(fromProxyResource)::map)
            .produce(ProxyResourceRepository::getProxyResources)
    rsocket(DELETE_OPEN_SHIFT_RESOURCE)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .responseMapper(fromOpenShiftResource)
            .handle(OpenShiftResourceRepository::deleteOpenShiftResource)
    rsocket(DELETE_ARTIFACTS_RESOURCE)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .responseMapper(fromArtifactsResource)
            .handle(ArtifactsResourceRepository::deleteArtifactsResource)
    rsocket(DELETE_GIT_RESOURCE)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .responseMapper(fromGitResource)
            .handle(GitResourceRepository::deleteGitResource)
    rsocket(DELETE_PROXY_RESOURCE)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .responseMapper(fromProxyResource)
            .handle(ProxyResourceRepository::deleteProxyResource)
}
