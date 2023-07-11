package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.platform.api.model.module.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ModuleInstallationRequest implements Validatable {
    private final Long projectId;
    private final ModuleConfiguration configuration;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("projectId", projectId, notNull())
                .validate("configuration", configuration, notNull());
    }
}
