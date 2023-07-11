package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.platform.api.model.assembly.*;
import ru.art.platform.api.model.project.*;
import ru.art.platform.api.model.resource.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class BuildRequest implements Validatable {
    private final Long projectId;
    private final ProjectVersion version;
    private final ResourceIdentifier resourceId;

    @Singular("artifactConfiguration")
    private final Set<ArtifactConfiguration> artifactConfigurations;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("projectId", projectId, notNull())
                .validate("version", version, notNull())
                .validate("resourceId", resourceId, notNull());
        resourceId.onValidating(validator);
    }
}
