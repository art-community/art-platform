package ru.art.platform.open.shift.constants

import com.openshift.restclient.model.IPod
import ru.art.platform.open.shift.constants.OpenShiftContainerStatus.UNKNOWN

object OpenShiftConstants {
    const val POD_GROUP = "pod.group"
    const val TCP = "TCP"
    const val UDP = "UDP"
    const val VERSION_V_1 = "v1"
    const val ROUTE_OPEN_SHIFT_VERSION_V_1 = "route.openshift.io/v1"
    const val IMAGE_OPEN_SHIFT_VERSION_V_1 = "image.openshift.io/v1"
    const val APPS_OPEN_SHIFT_VERSION_V_1 = "apps.openshift.io/v1"
    const val ROUTE_PREFIX = "route"
    const val SERVICE_PREFIX = "service"
    const val TO = "to"
    const val DATA = "data"
    const val GROUP_VERSION = "groupVersion"
    const val API_GROUP = "apiGroup"
    const val AUTHORIZATION_GROUP = "rbac.authorization.k8s.io"
    const val AUTHORIZATION_GROUP_VERSION = "$AUTHORIZATION_GROUP/$VERSION_V_1"
    const val CLUSTER_ROLE_KIND = "ClusterRole"
    const val SYSTEM_IMAGE_PULLER = "system:image-puller"
    const val SYSTEM_IMAGE_BUILDER = "system:image-builder"
    const val SYSTEM_SERVICE_ACCOUNTS = "system:serviceaccounts"
    const val SPEC = "spec"
    const val PORTS = "ports"
    const val NODE_PORT = "nodePort"
    const val TARGET_PORT = "targetPort"
    const val CLUSTER_IP = "clusterIP"
    const val INIT_CONTAINERS = "initContainers"
    const val PORT = "port"
    const val PATH = "path"
    const val VOLUMES = "volumes"
    const val NAME = "name"
    const val EMPTY_DIR = "emptyDir"
    const val CONFIG_MAP = "configMap"
    const val SECRET = "secret"
    const val SECRET_NAME = "secretName"
    const val POD_POLLING_DELAY_MILLIS = 2000L
    const val POD_CHECK_WAITING_MILLIS = 1000L
    const val POD_CHECKING_TIMEOUT = 10 * 60 * 1000L
    const val SERVICE_ACCOUNT_TOKEN_PATH = "/var/run/secrets/kubernetes.io/serviceaccount/token"
    const val SERVICE_ACCOUNT = "serviceaccount"
    const val SUBJECTS = "subjects"
    const val TEMPLATE = "template"
    const val NODE_NAME = "nodeName"
    const val SECRET_DATA = "data"
    const val NODE_SELECTOR = "nodeSelector"
    const val OPAQUE_SECRET_TYPE = "Opaque"
    const val IMAGE_PULL_POLICY_ALWAYS = "Always"
    const val LATEST_TAG = "latest"
    const val IMAGE_PULL_POLICY_IF_NOT_PRESENT = "IfNotPresent"
    const val IMAGE_PULL_POLICY_NEVER = "Never"
    const val DEPLOY_POD_POSTFIX = "-deploy"
    const val READINESS_PROBE = "readinessProbe"
    const val LIVENESS_PROBE = "livenessProbe"
    const val HTTP_GET = "httpGet"
    const val SCHEME = "scheme"
    const val HTTP = "HTTP"
    const val CONTAINERS = "containers"
    const val INITIAL_DELAY_SECONDS = "initialDelaySeconds";
    const val TIMEOUT_SECONDS = "timeoutSeconds";
    const val PERIOD_SECONDS = "periodSeconds";
    const val SUCCESS_THRESHOLD = "successThreshold";
    const val FAILURE_THRESHOLD = "failureThreshold";
}

enum class OpenShiftContainerStatus(val status: String) {
    CRASH_LOOP_BACK_OFF("CrashLoopBackOff"),
    ERROR("Error"),
    RUNNING("Running"),
    READY("Ready"),
    ACTIVE("Active"),
    IMAGE_PULL_BACK_OFF("ImagePullBackOff"),
    TERMINATING("Terminating"),
    CREATED("Created"),
    STARTED("Started"),
    FAILED("Failed"),
    KILLING("Killing"),
    PREEMPT("Preempting"),
    BACK_OFF("BackOff"),
    EXCEEDED_GRACE_PERIOD("ExceededGracePeriod"),
    FAILED_TO_CREATE_POD("FailedCreatePodContainer"),
    FAILED_TO_KILL_POD("FailedKillPod"),
    NETWORK_NOT_READY("NetworkNotReady"),
    PULLING_IMAGE("Pulling"),
    PULLED_IMAGE("Pulled"),
    FAILED_TO_INSPECT_IMAGE("InspectFailed"),
    ERR_IMAGE_NEVER_PULL_POLICY("ErrImageNeverPull"),
    NODE_READY("NodeReady"),
    NODE_NOT_READY("NodeNotReady"),
    NODE_SCHEDULABLE("NodeSchedulable"),
    NODE_NOT_SCHEDULABLE("NodeNotSchedulable"),
    STARTING("Starting"),
    KUBELET_SETUP_FAILED("KubeletSetupFailed"),
    FAILED_ATTACH_VOLUME("FailedAttachVolume"),
    FAILED_MOUNT_VOLUME("FailedMount"),
    VOLUME_RESIZE_FAILED("VolumeResizeFailed"),
    VOLUME_RESIZE_SUCCESS("VolumeResizeSuccessful"),
    FILE_SYSTEM_RESIZE_FAILED("FileSystemResizeFailed"),
    FILE_SYSTEM_RESIZE_SUCCESS("FileSystemResizeSuccessful"),
    FAILED_MAP_VOLUME("FailedMapVolume"),
    WARN_ALREADY_MOUNTED_VOLUME("AlreadyMountedVolume"),
    SUCCESSFUL_ATTACH_VOLUME("SuccessfulAttachVolume"),
    SUCCESSFUL_MOUNT_VOLUME("SuccessfulMountVolume"),
    REBOOTED("Rebooted"),
    CONTAINER_G_C_FAILED("ContainerGCFailed"),
    IMAGE_G_C_FAILED("ImageGCFailed"),
    FAILED_NODE_ALLOCATABLE_ENFORCEMENT("FailedNodeAllocatableEnforcement"),
    SUCCESSFUL_NODE_ALLOCATABLE_ENFORCEMENT("NodeAllocatableEnforced"),
    SANDBOX_CHANGED("SandboxChanged"),
    FAILED_CREATE_POD_SAND_BOX("FailedCreatePodSandBox"),
    FAILED_STATUS_POD_SAND_BOX("FailedPodSandBoxStatus"),
    FAILED_MOUNT_ON_FILESYSTEM_MISMATCH("FailedMountOnFilesystemMismatch"),
    INVALID_DISK_CAPACITY("InvalidDiskCapacity"),
    FREE_DISK_SPACE_FAILED("FreeDiskSpaceFailed"),
    CONTAINER_UNHEALTHY("Unhealthy"),
    CONTAINER_PROBE_WARNING("ProbeWarning"),
    FAILED_SYNC("FailedSync"),
    FAILED_VALIDATION("FailedValidation"),
    FAILED_POST_START_HOOK("FailedPostStartHook"),
    FAILED_PRE_STOP_HOOK("FailedPreStopHook"),
    UNKNOWN("Unknown");

    fun failed() = this == CRASH_LOOP_BACK_OFF || this == ERROR || this == IMAGE_PULL_BACK_OFF

    fun terminating() = this == TERMINATING

    fun ready() = this == READY || this == RUNNING
}

fun IPod.containerStatus(): OpenShiftContainerStatus = OpenShiftContainerStatus.values()
        .find { containerStatus -> containerStatus.status.toLowerCase() == status.toLowerCase() }
        ?: UNKNOWN
