package ru.art.platform.api.model.load;

import lombok.*;
import ru.art.platform.api.model.file.*;
import ru.art.platform.api.model.project.*;
import ru.art.platform.api.model.resource.*;
import ru.art.platform.api.model.variable.*;
import static java.time.Instant.*;
import static ru.art.platform.common.constants.States.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class LoadTest {
    private final Long id;
    private final Long projectId;
    private final Long scenarioId;
    private final ProjectVersion version;
    @Builder.Default
    private final Long startTimeStamp = now().getEpochSecond();
    private final Long endTimeStamp;
    @Builder.Default
    private final String state = LOAD_TEST_STARTED_STATE;
    private final ResourceIdentifier resourceId;
    private final Long logId;
    private final PlatformFileIdentifier reportArchiveName;
    @Singular("environmentVariable")
    private final List<EnvironmentVariable> environmentVariables;

    public boolean isRunning() {
        return getState().equals(LOAD_TEST_STARTED_STATE)
                || getState().equals(LOAD_TEST_RUNNING_STATE)
                || getState().equals(LOAD_TEST_STARTED_ON_RESOURCE_STATE);
    }

    public boolean isCanceled() {
        return getState().equals(LOAD_TEST_CANCELED_STATE);
    }
}
