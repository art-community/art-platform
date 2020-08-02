package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.platform.api.model.project.*;
import ru.art.platform.api.model.resource.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class AgentProjectChangesRequest implements Validatable {
    private final Project project;
    private final GitResource gitResource;
    private final String reference;
    private final String fromHash;
    private final String toHash;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("project", project, notNull())
                .validate("reference", reference, notEmptyString())
                .validate("gitResource", gitResource, notNull());
    }
}
