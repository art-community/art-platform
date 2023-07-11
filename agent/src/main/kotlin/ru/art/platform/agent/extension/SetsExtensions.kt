package ru.art.platform.agent.extension

import ru.art.platform.api.model.assembly.ArtifactArchiveConfiguration
import ru.art.platform.api.model.assembly.ArtifactConfiguration
import ru.art.platform.api.model.resource.*


fun Set<OpenShiftResource>.findOpenShiftResource(resourceId: ResourceIdentifier) =
        single { resource -> resource.id == resourceId.id }

fun Set<ArtifactsResource>.findArtifactsResource(resourceId: ResourceIdentifier) =
        single { resource -> resource.id == resourceId.id }

fun Set<ProxyResource>.findProxyResource(resourceId: ResourceIdentifier) =
        single { resource -> resource.id == resourceId.id }

fun Set<ArtifactConfiguration>.findArtifactConfiguration(name: String) =
        single { configuration -> configuration.artifact.name == name }

fun Set<ArtifactArchiveConfiguration>.findArchiveConfiguration(resourceId: ResourceIdentifier) =
        single { configuration -> configuration.resourceId == resourceId }
