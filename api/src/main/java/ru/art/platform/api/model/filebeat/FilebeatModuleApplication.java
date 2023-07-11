package ru.art.platform.api.model.filebeat;

import lombok.*;
import ru.art.platform.api.model.application.*;
import ru.art.platform.api.model.resource.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class FilebeatModuleApplication {
    private final ApplicationIdentifier applicationId;
    private final String url;
    private final ResourceIdentifier resourceId;
    private final int port;

    @EqualsAndHashCode.Exclude
    private final String configuration;
}
