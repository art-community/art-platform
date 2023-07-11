package ru.art.platform.api.model.assembly;

import lombok.*;
import ru.art.platform.api.model.project.*;
import ru.art.platform.api.model.resource.*;
import static java.time.Instant.*;
import static ru.art.platform.common.constants.States.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class Assembly {
    private final Long id;
    private final Long projectId;
    private final String technology;
    private final ProjectVersion version;
    private final ResourceIdentifier resourceId;
    private final Long logId;
    private final Long endTimeStamp;

    @Builder.Default
    private final Long startTimeStamp = now().getEpochSecond();

    @Builder.Default
    private final String state = ASSEMBLY_STARTED_STATE;

    @Singular("artifact")
    @EqualsAndHashCode.Exclude
    private final Set<AssembledArtifact> artifacts;

    @Singular("artifactConfiguration")
    @EqualsAndHashCode.Exclude
    private final Set<ArtifactConfiguration> artifactConfigurations;

    public boolean isRunning() {
        return getState().equals(ASSEMBLY_STARTED_STATE)
                || getState().equals(ASSEMBLY_RESTARTED_STATE)
                || getState().equals(ASSEMBLY_BUILDING_STATE)
                || getState().equals(ASSEMBLY_STARTED_ON_RESOURCE_STATE);
    }

    public boolean isCanceled() {
        return getState().equals(ASSEMBLY_CANCELED_STATE);
    }
}
