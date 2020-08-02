package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.platform.api.model.project.*;
import ru.art.platform.api.model.resource.*;
import ru.art.platform.api.model.variable.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class LoadTestRequest implements Validatable {
    private final Long scenarioId;
    private final ResourceIdentifier resourceId;
    private final Long projectId;
    private final ProjectVersion version;

    @Singular("environmentVariable")
    private final List<EnvironmentVariable> environmentVariables;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("resourceId", resourceId, notNull())
                .validate("projectId", projectId, notNull())
                .validate("scenarioId", scenarioId, notNull())
                .validate("version", version, notNull());
    }
}
