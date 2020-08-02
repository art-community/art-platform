package ru.art.platform.api.model.assembly;

import lombok.*;
import ru.art.platform.api.model.project.*;
import ru.art.platform.api.model.resource.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class AssemblyInformation {
    private final Long id;
    private final Long projectId;
    private final String technology;
    private final ProjectVersion version;
    private final String state;
    private final Long startTimeStamp;
    private final Long endTimeStamp;
    private final ResourceIdentifier resourceId;
    private final Long logId;

    @Singular("artifact")
    @EqualsAndHashCode.Exclude
    private final List<AssembledArtifact> artifacts;
}
