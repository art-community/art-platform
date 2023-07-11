package ru.art.platform.open.shift.service

import com.openshift.internal.restclient.model.*
import com.openshift.restclient.ResourceKind.*
import com.openshift.restclient.model.*
import ru.art.logging.LoggingModule.*
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.platform.open.shift.constants.OpenShiftConstants.IMAGE_OPEN_SHIFT_VERSION_V_1

fun OpenShiftService.createImageStream(name: String, namespace: String, labels: Map<String, String> = emptyMap()): IImageStream =
        with(client.resourceFactory.create<ImageStream>(IMAGE_OPEN_SHIFT_VERSION_V_1, IMAGE_STREAM, name)) {
            setNamespace(namespace)
            labels.forEach(::addLabel)
            loggingModule().getLogger(OpenShiftService::class.java).info("Creating image stream:\n${toJson()}")
            client.create(this)
        }

fun OpenShiftService.getImageStream(name: String, namespace: String) =
        client.list<IImageStream>(IMAGE_STREAM, namespace).find { stream -> stream.name == name }

fun OpenShiftService.deleteImageStream(name: String, namespace: String) =
        getImageStream(name, namespace)
                ?.let(client::delete)
                ?.let {
                    loggingModule().getLogger(OpenShiftService::class.java).info("Deleting image stream '$namespace.$name'")
                }

fun OpenShiftProjectService.createImageStream(name: String, labels: Map<String, String> = emptyMap()): IImageStream =
        createImageStream(name, project.namespaceName, labels)

fun OpenShiftProjectService.getImageStream(name: String) =
        getImageStream(name, project.namespaceName)

fun OpenShiftProjectService.deleteImageStream(name: String) =
        deleteImageStream(name, project.namespaceName)

fun OpenShiftService.getRegistryUrlByPlatformAgentImage(): String =
        getImageStream(AGENT_CAMEL_CASE, PLATFORM_CAMEL_CASE)!!.dockerImageRepository.repositoryHost