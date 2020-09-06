type OpenShiftLabel = {
    name: string
    value: string
}

type OpenShiftPodConfiguration = {
    nodeSelector: OpenShiftLabel[]
}
