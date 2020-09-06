package ru.art.platform.factory

import ru.art.platform.api.model.external.ExternalIdentifier
import ru.art.platform.api.model.project.Project
import ru.art.platform.api.model.request.ProjectRequest
import ru.art.platform.api.model.resource.ResourceIdentifier
import ru.art.platform.common.constants.Resources.GIT_RESOURCE
import ru.art.platform.common.extensions.normalizeNameToId
import ru.art.platform.repository.GitResourceRepository.getGitResource

object ProjectFactory {
    fun createProject(request: ProjectRequest): Project = Project.builder()
            .name(request.name)
            .initializationResourceId(request.initializationResourceId)
            .externalId(ExternalIdentifier.builder()
                    .id(request.name.normalizeNameToId())
                    .resourceId(request.initializationResourceId)
                    .build())
            .gitResourceId(ResourceIdentifier.builder()
                    .id(request.gitResourceId)
                    .name(getGitResource(request.gitResourceId).name)
                    .type(GIT_RESOURCE)
                    .build())
            .openShiftConfiguration(request.openShiftConfiguration)
            .notificationsConfiguration(request.notificationsConfiguration)
            .build()
}
