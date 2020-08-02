package ru.art.platform.open.shift.service

import com.openshift.internal.restclient.model.ConfigMap
import com.openshift.internal.restclient.model.ModelNodeBuilder
import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys.*
import com.openshift.restclient.ResourceKind.CONFIG_MAP
import com.openshift.restclient.model.IConfigMap
import ru.art.logging.LoggingModule.loggingModule
import ru.art.platform.open.shift.constants.OpenShiftConstants.DATA
import ru.art.platform.open.shift.constants.OpenShiftConstants.VERSION_V_1

fun OpenShiftService.createConfigMap(name: String, content: Map<String, String>, namespace: String): IConfigMap =
        client.create(ConfigMap(with(ModelNodeBuilder()) {
            set(KIND, CONFIG_MAP)
            set(APIVERSION, VERSION_V_1)
            set(METADATA_NAME, name)
            set(METADATA_NAMESPACE, namespace)
            build().apply {
                content.forEach { (key, value) -> get(DATA, key).set(value) }
            }
        }, client, mapOf()).also { configMap ->
            loggingModule().getLogger(OpenShiftService::class.java).info("Creating config map:\n$configMap")
        })

fun OpenShiftService.putConfigMapContent(name: String, content: Map<String, String>, namespace: String): IConfigMap? =
        getConfigMap(name, namespace)?.let { current ->
            current.data.putAll(content)
            client.update<IConfigMap>(current).also { configMap ->
                loggingModule().getLogger(OpenShiftService::class.java).info("Updating config map:\n$configMap")
            }
        }

fun OpenShiftService.getConfigMap(name: String, namespace: String) =
        client.list<IConfigMap>(CONFIG_MAP, namespace).find { map -> map.name == name }

fun OpenShiftService.getConfigMaps(namespace: String): List<IConfigMap> =
        client.list(CONFIG_MAP, namespace)

fun OpenShiftService.deleteConfigMap(name: String, namespace: String) =
        getConfigMap(name, namespace)
                ?.let { client.delete(CONFIG_MAP, namespace, name) }
                ?.let { loggingModule().getLogger(OpenShiftService::class.java).info("Deleting config map '$namespace.$name'") }

fun OpenShiftProjectService.createConfigMap(name: String, content: Map<String, String>): IConfigMap =
        createConfigMap(name, content, project.namespaceName)

fun OpenShiftProjectService.getConfigMap(name: String) =
        getConfigMap(name, project.namespaceName)

fun OpenShiftProjectService.getConfigMaps(): List<IConfigMap> =
        getConfigMaps(project.namespaceName)

fun OpenShiftProjectService.deleteConfigMap(name: String) =
        deleteConfigMap(name, project.namespaceName)

fun OpenShiftProjectService.putConfigMapContent(name: String, content: Map<String, String>) =
        putConfigMapContent(name, content, project.namespaceName)
