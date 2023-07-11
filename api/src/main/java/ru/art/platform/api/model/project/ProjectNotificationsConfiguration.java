package ru.art.platform.api.model.project;

import lombok.*;
import ru.art.platform.api.model.resource.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProjectNotificationsConfiguration {
    private final String url;
    private final String additionalMessage;
    private final ResourceIdentifier proxyId;
}
