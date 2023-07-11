package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.platform.api.model.module.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ModuleUpdateRequest implements Validatable {
    private final Long moduleId;
    private final ModuleConfiguration newModuleConfiguration;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("moduleId", moduleId, notNull())
                .validate("newModuleConfiguration", newModuleConfiguration, notNull());
    }
}
