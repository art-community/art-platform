package ru.art.platform.repository

import ru.art.platform.api.mapping.external.ExternalArtifactMapper.fromExternalArtifact
import ru.art.platform.api.mapping.external.ExternalArtifactMapper.toExternalArtifact
import ru.art.platform.api.model.external.ExternalArtifact
import ru.art.platform.common.constants.PlatformKeywords.PLATFORM_CAMEL_CASE
import ru.art.platform.constants.DbConstants.EXTERNAL_ARTIFACT_SPACE
import ru.art.tarantool.dao.TarantoolDao.tarantool

object ExternalArtifactRepository {
    fun putExternalArtifact(artifact: ExternalArtifact): ExternalArtifact =
            toExternalArtifact.map(tarantool(PLATFORM_CAMEL_CASE).put(EXTERNAL_ARTIFACT_SPACE, fromExternalArtifact.map(artifact)))

    fun getExternalArtifacts(): Set<ExternalArtifact> = tarantool(PLATFORM_CAMEL_CASE)
            .selectAll(EXTERNAL_ARTIFACT_SPACE)
            .map(toExternalArtifact::map)
            .toSet()

    fun getExternalArtifacts(projectId: Long): Set<ExternalArtifact> = getExternalArtifacts().filter { artifact -> artifact.projectId == projectId }.toSet()
}