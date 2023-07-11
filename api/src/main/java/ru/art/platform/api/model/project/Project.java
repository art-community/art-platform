package ru.art.platform.api.model.project;

import lombok.*;
import ru.art.platform.api.model.external.*;
import ru.art.platform.api.model.resource.*;
import static java.time.Instant.*;
import static ru.art.platform.common.constants.States.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Project {
    @EqualsAndHashCode.Include
    private final Long id;
    @EqualsAndHashCode.Include
    private final String name;
    private final ResourceIdentifier gitResourceId;
    private final ResourceIdentifier initializationResourceId;
    private final ProjectOpenShiftConfiguration openShiftConfiguration;
    private final ProjectNotificationsConfiguration notificationsConfiguration;
    private final ExternalIdentifier externalId;

    @Builder.Default
    private final String state = PROJECT_CREATED_STATE;

    @Builder.Default
    private final Long creationTimeStamp = now().getEpochSecond();

    @Singular("technology")
    private final List<String> technologies;

    @Singular("version")
    private final Set<ProjectVersion> versions;

    @Singular("artifact")
    private final Set<ProjectArtifact> artifacts;
}
