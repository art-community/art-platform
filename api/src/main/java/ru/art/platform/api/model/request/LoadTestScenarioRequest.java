package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.platform.api.model.gradle.*;
import ru.art.platform.api.model.resource.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class LoadTestScenarioRequest implements Validatable {
    private final String name;
    private final ResourceIdentifier defaultResourceId;
    private final Long projectId;
    private final String launchTechnology;
    private final String reportTechnology;
    private final GradleAssemblyConfiguration gradleConfiguration;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("defaultResourceId", defaultResourceId, notNull())
                .validate("name", name, notNull())
                .validate("projectId", projectId, notNull())
                .validate("launchTechnology", launchTechnology, notNull())
                .validate("gradleConfiguration", gradleConfiguration, notNull())
                .validate("reportTechnology", reportTechnology, notNull());
    }
}
