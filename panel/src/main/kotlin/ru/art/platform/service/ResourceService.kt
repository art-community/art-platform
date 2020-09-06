package ru.art.platform.service

import ru.art.platform.api.model.request.ArtifactsResourceRequest
import ru.art.platform.api.model.request.GitResourceRequest
import ru.art.platform.api.model.request.OpenShiftResourceRequest
import ru.art.platform.api.model.request.ProxyResourceRequest
import ru.art.platform.api.model.resource.*
import ru.art.platform.common.constants.ErrorCodes.RESOURCE_ALREADY_EXISTS
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.factory.ResourceIdFactory.artifactsResourceId
import ru.art.platform.factory.ResourceIdFactory.gitResourceId
import ru.art.platform.factory.ResourceIdFactory.openShiftResourceId
import ru.art.platform.factory.ResourceIdFactory.platformResourceId
import ru.art.platform.factory.ResourceIdFactory.proxyResourceId
import ru.art.platform.repository.ArtifactsResourceRepository.getArtifactsResource
import ru.art.platform.repository.ArtifactsResourceRepository.getArtifactsResources
import ru.art.platform.repository.ArtifactsResourceRepository.putArtifactsResource
import ru.art.platform.repository.GitResourceRepository.getGitResource
import ru.art.platform.repository.GitResourceRepository.getGitResources
import ru.art.platform.repository.GitResourceRepository.putGitResource
import ru.art.platform.repository.OpenShiftResourceRepository.getOpenShiftResource
import ru.art.platform.repository.OpenShiftResourceRepository.getOpenShiftResources
import ru.art.platform.repository.OpenShiftResourceRepository.putOpenShiftResource
import ru.art.platform.repository.PlatformResourceRepository.getPlatformResources
import ru.art.platform.repository.ProxyResourceRepository.getProxyResource
import ru.art.platform.repository.ProxyResourceRepository.getProxyResources
import ru.art.platform.repository.ProxyResourceRepository.putProxyResource


object ResourceService {
    fun addOpenShiftResource(request: OpenShiftResourceRequest): OpenShiftResource {
        getOpenShiftResource(request.name).ifPresent { throw PlatformException(RESOURCE_ALREADY_EXISTS) }
        val resource = OpenShiftResource.builder()
                .name(request.name)
                .userName(request.userName)
                .password(request.password)
                .apiUrl(request.apiUrl)
                .applicationsDomain(request.applicationsDomain)
                .privateRegistryUrl(request.privateRegistryUrl)
                .build()
        return putOpenShiftResource(resource)
    }

    fun addArtifactsResource(request: ArtifactsResourceRequest): ArtifactsResource {
        getArtifactsResource(request.name).ifPresent { throw PlatformException(RESOURCE_ALREADY_EXISTS) }
        val resource = ArtifactsResource.builder()
                .name(request.name)
                .url(request.url)
                .userName(request.userName)
                .password(request.password)
                .build()
        return putArtifactsResource(resource)
    }

    fun addGitResource(request: GitResourceRequest): GitResource {
        getGitResource(request.name).ifPresent { throw PlatformException(RESOURCE_ALREADY_EXISTS) }
        val resource = GitResource.builder()
                .name(request.name)
                .url(request.url)
                .userName(request.userName)
                .password(request.password)
                .build()
        return putGitResource(resource)
    }

    fun addProxyResource(request: ProxyResourceRequest): ProxyResource {
        getProxyResource(request.name).ifPresent { throw PlatformException(RESOURCE_ALREADY_EXISTS) }
        val resource = ProxyResource.builder()
                .name(request.name)
                .host(request.host)
                .port(request.port)
                .userName(request.userName)
                .password(request.password)
                .build()
        return putProxyResource(resource)
    }

    fun updateOpenShiftResource(request: OpenShiftResource): OpenShiftResource {
        val resource = getOpenShiftResource(request.id)
        if (request.name != resource.name && getOpenShiftResource(request.name).isPresent) {
            throw PlatformException(RESOURCE_ALREADY_EXISTS)
        }
        return putOpenShiftResource(request)

    }

    fun updateArtifactsResource(request: ArtifactsResource): ArtifactsResource {
        val resource = getArtifactsResource(request.id)
        if (request.name != resource.name && getArtifactsResource(request.name).isPresent) {
            throw PlatformException(RESOURCE_ALREADY_EXISTS)
        }
        return putArtifactsResource(request)
    }

    fun updateGitResource(request: GitResource): GitResource {
        val resource = getGitResource(request.id)
        if (request.name != resource.name && getGitResource(request.name).isPresent) {
            throw PlatformException(RESOURCE_ALREADY_EXISTS)
        }
        return putGitResource(request)
    }

    fun updateProxyResource(request: ProxyResource): ProxyResource {
        val resource = getProxyResource(request.id)
        if (request.name != resource.name && getProxyResource(request.name).isPresent) {
            throw PlatformException(RESOURCE_ALREADY_EXISTS)
        }
        return putProxyResource(request)
    }

    fun getResourceIds(): Set<ResourceIdentifier> =
            getOpenShiftResources().map { resource -> openShiftResourceId(resource.id, resource.name) }.toSet() +
                    getArtifactsResources().map { resource -> artifactsResourceId(resource.id, resource.name) }.toSet() +
                    getPlatformResources().map { resource -> platformResourceId(resource.id, resource.name) }.toSet() +
                    getGitResources().map { resource -> gitResourceId(resource.id, resource.name) }.toSet() +
                    getProxyResources().map { resource -> proxyResourceId(resource.id, resource.name) }.toSet()
}
