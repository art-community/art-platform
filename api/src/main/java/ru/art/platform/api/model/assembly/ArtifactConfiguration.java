package ru.art.platform.api.model.assembly;

import lombok.*;
import ru.art.platform.api.model.gradle.*;
import ru.art.platform.api.model.project.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ArtifactConfiguration {
    private final String name;
    private final ProjectArtifact artifact;
    private final Set<ArtifactArchiveConfiguration> archives;

    @EqualsAndHashCode.Exclude
    private final GradleArtifactConfiguration gradleConfiguration;
}
