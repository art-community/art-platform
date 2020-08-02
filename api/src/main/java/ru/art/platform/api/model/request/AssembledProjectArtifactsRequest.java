package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class AssembledProjectArtifactsRequest implements Validatable {
    private final Long projectId;
    private final String version;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("projectId", projectId, notNull());
    }
}
