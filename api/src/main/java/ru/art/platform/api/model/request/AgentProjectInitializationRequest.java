package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.platform.api.model.project.*;
import ru.art.platform.api.model.resource.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class AgentProjectInitializationRequest implements Validatable {
    private final Project project;
    private final GitResource gitResource;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("project", project, notNull())
                .validate("gitResource", gitResource, notNull());
    }
}
