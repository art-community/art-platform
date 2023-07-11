package ru.art.platform.open.shift.service

import com.openshift.internal.restclient.model.Secret
import com.openshift.restclient.ResourceKind.SECRET
import com.openshift.restclient.model.secret.ISecret
import okhttp3.internal.EMPTY_BYTE_ARRAY
import ru.art.core.extension.ExceptionExtensions.ifException
import ru.art.logging.LoggingModule.loggingModule
import ru.art.platform.open.shift.constants.OpenShiftConstants.OPAQUE_SECRET_TYPE
import ru.art.platform.open.shift.constants.OpenShiftConstants.SECRET_DATA
import ru.art.platform.open.shift.constants.OpenShiftConstants.VERSION_V_1

fun OpenShiftProjectService.createSecret(name: String, content: Map<String, ByteArray>): ISecret =
        createSecret(name, content, project.namespaceName)

fun OpenShiftService.createSecret(name: String, content: Map<String, ByteArray>, namespace: String): ISecret =
        with(client.resourceFactory.create<Secret>(VERSION_V_1, SECRET, name)) {
            content.forEach { (key, value) -> addData(key, value) }
            setNamespace(namespace)
            type = OPAQUE_SECRET_TYPE
            loggingModule().getLogger(OpenShiftService::class.java).info("Creating secret $name with keys: ${content.keys}")
            return client.create(this)
        }

fun OpenShiftService.getSecret(name: String, namespace: String) =
        client.list<Secret>(SECRET, namespace).find { map -> map.name == name }


fun OpenShiftProjectService.getSecret(name: String) = getSecret(name, project.namespaceName)

fun Secret.extractSecretData(dataKey: String): ByteArray = ifException({ getData(dataKey) ?: EMPTY_BYTE_ARRAY}, EMPTY_BYTE_ARRAY)

fun Secret.size() = node[SECRET_DATA]?.keys()?.size ?: 0

fun OpenShiftService.deleteSecret(name: String, namespace: String) =
        getSecret(name, namespace)
                ?.let { client.delete(SECRET, namespace, name) }
                ?.let { loggingModule().getLogger(OpenShiftService::class.java).info("Deleting secret '$namespace.$name'") }

fun OpenShiftProjectService.deleteSecret(name: String) =
        deleteSecret(name, project.namespaceName)
