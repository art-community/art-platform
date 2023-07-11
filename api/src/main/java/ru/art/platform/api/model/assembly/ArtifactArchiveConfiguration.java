package ru.art.platform.api.model.assembly;

import lombok.*;
import ru.art.platform.api.model.docker.*;
import ru.art.platform.api.model.resource.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ArtifactArchiveConfiguration {
    private final String archiveTechnology;
    private final ResourceIdentifier resourceId;

    @EqualsAndHashCode.Exclude
    private final DockerImageConfiguration dockerConfiguration;
}
