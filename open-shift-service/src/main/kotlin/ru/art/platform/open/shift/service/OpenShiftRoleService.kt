package ru.art.platform.open.shift.service

import com.openshift.internal.restclient.model.KubernetesResource
import com.openshift.internal.restclient.model.ModelNodeBuilder
import com.openshift.internal.restclient.model.ObjectReference
import com.openshift.internal.restclient.model.authorization.RoleBinding
import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys.*
import com.openshift.restclient.ResourceKind
import com.openshift.restclient.ResourceKind.ROLE_BINDING
import org.jboss.dmr.ModelNode
import ru.art.platform.open.shift.constants.OpenShiftConstants.API_GROUP
import ru.art.platform.open.shift.constants.OpenShiftConstants.AUTHORIZATION_GROUP
import ru.art.platform.open.shift.constants.OpenShiftConstants.AUTHORIZATION_GROUP_VERSION
import ru.art.platform.open.shift.constants.OpenShiftConstants.CLUSTER_ROLE_KIND
import ru.art.platform.open.shift.constants.OpenShiftConstants.GROUP_VERSION
import ru.art.platform.open.shift.constants.OpenShiftConstants.SUBJECTS
import ru.art.platform.open.shift.constants.OpenShiftConstants.SYSTEM_IMAGE_BUILDER
import ru.art.platform.open.shift.constants.OpenShiftConstants.SYSTEM_IMAGE_PULLER
import ru.art.platform.open.shift.constants.OpenShiftConstants.SYSTEM_SERVICE_ACCOUNTS

fun OpenShiftService.createClusterRoleGroupBinding(role: String, group: String, namespace: String): KubernetesResource =
        client.create(RoleBinding(with(ModelNodeBuilder()) {
            set(KIND, ROLE_BINDING)
            set(GROUP_VERSION, AUTHORIZATION_GROUP_VERSION)
            set(METADATA_NAME, "$role-to-$group")
            set(METADATA_NAMESPACE, namespace)
            set(SUBJECTS, ModelNode().add(ModelNodeBuilder()
                    .set(KIND, ResourceKind.GROUP)
                    .set(API_GROUP, AUTHORIZATION_GROUP)
                    .set(NAME, group)
                    .build()))
            build()
        }, client, emptyMap()).apply {
            addUserName(group)
            roleRef = ObjectReference(ModelNodeBuilder()
                    .set(NAME, role)
                    .set(API_GROUP, AUTHORIZATION_GROUP)
                    .set(KIND, CLUSTER_ROLE_KIND)
                    .build())
        })

fun OpenShiftService.bindClusterRoleToGroup(role: String, group: String, namespace: String): KubernetesResource =
        getRoleBinding("$role-to-$group", namespace) ?: createClusterRoleGroupBinding(role, group, namespace)

fun OpenShiftService.getRoleBinding(name: String, namespace: String) =
        client.list<KubernetesResource>(ROLE_BINDING, namespace).find { binding -> binding.name == name }

fun OpenShiftService.allowImagesPulling(imageSourceProject: String, imageTargetProject: String) =
        bindClusterRoleToGroup(SYSTEM_IMAGE_PULLER, "$SYSTEM_SERVICE_ACCOUNTS:$imageTargetProject", imageSourceProject)

fun OpenShiftService.allowImagesPushing(builderProject: String, targetProject: String) =
        bindClusterRoleToGroup(SYSTEM_IMAGE_BUILDER, "$SYSTEM_SERVICE_ACCOUNTS:$builderProject", targetProject)