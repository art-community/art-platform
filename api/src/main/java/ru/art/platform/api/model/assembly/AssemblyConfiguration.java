package ru.art.platform.api.model.assembly;

import lombok.*;
import ru.art.platform.api.model.gradle.*;
import ru.art.platform.api.model.resource.*;
import ru.art.service.validation.*;
import static ru.art.platform.common.constants.GradleVersions.*;
import static ru.art.platform.common.constants.Technologies.*;
import static ru.art.service.validation.ValidationExpressions.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class AssemblyConfiguration implements Validatable {
    private final Long id;
    private final ResourceIdentifier defaultResourceId;
    private final String technology;
    private final GradleAssemblyConfiguration gradleConfiguration;

    @Singular("artifactConfiguration")
    private final List<ArtifactConfiguration> artifactConfigurations;


    @Override
    public void onValidating(Validator validator) {
        validator
                .validate("id", id, notNull())
                .validate("defaultBuildResourceId", defaultResourceId, notNull())
                .validate("technology", technology, notEmptyString());
        defaultResourceId.onValidating(validator);
        if (GRADLE.equalsIgnoreCase(technology)) {
            validator
                    .validate("gradleConfiguration", gradleConfiguration, notNull())
                    .validate("gradleConfiguration.gradleVersion", gradleConfiguration.getVersion(),
                            notEmptyString(),
                            containsOther(GRADLE_VERSIONS));
        }
    }
}
