package ru.art.platform.api.model.network;


import lombok.*;
import ru.art.platform.api.model.openShift.*;
import ru.art.platform.api.model.resource.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class NetworkAccessRequest {
    private final ResourceIdentifier resourceId;
    private final String hostName;
    private final int port;
    private final int timeout;
    private final OpenShiftPodConfiguration openShiftPodConfiguration;
}
