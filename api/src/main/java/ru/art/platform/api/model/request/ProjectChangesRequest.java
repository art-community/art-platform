package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.platform.api.model.project.*;
import ru.art.platform.api.model.resource.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProjectChangesRequest implements Validatable {
    private final Long projectId;
    private final String reference;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("projectId", projectId, notNull());
    }
}
