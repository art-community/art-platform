package ru.art.platform.panel.service

import ru.art.entity.Entity
import ru.art.platform.panel.constants.BitBucketConstants.CHANGES
import ru.art.platform.panel.constants.BitBucketConstants.REF_ID
import ru.art.platform.panel.constants.BitBucketConstants.REPOSITORY_NAME
import ru.art.platform.panel.constants.BitBucketConstants.REPOSITORY_PROJECT_KEY
import ru.art.platform.git.constants.GitConstants.DOT_GIT
import ru.art.platform.git.constants.GitConstants.REFS_HEADS
import ru.art.platform.panel.repository.GitResourceRepository.getGitResource
import ru.art.platform.panel.repository.ProjectRepository.getProjects
import ru.art.platform.panel.service.AutomationService.handleProjectsChanges

object BitBucketService {
    fun onEvent(requestEntity: Entity?) {
//        requestEntity?.let { request ->
//            val projectKey = request.findString(REPOSITORY_PROJECT_KEY).toLowerCase()
//            val repositoryName = request.findString(REPOSITORY_NAME).toLowerCase()
//            val projects = getProjects().filter { project ->
//                val gitUrl = getGitResource(project.gitResourceId.id).url.toLowerCase()
//                gitUrl.contains(projectKey) && (gitUrl.endsWith(repositoryName) || gitUrl.endsWith("$repositoryName$DOT_GIT"))
//            }
//            request.findEntityList(CHANGES).forEach { changes -> handleProjectsChanges(projects, changes.findString(REF_ID).substringAfter(REFS_HEADS)) }
//        }
    }
}

