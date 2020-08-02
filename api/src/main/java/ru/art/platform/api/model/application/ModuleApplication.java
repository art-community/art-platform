package ru.art.platform.api.model.application;

import lombok.Value;
import lombok.*;
import ru.art.entity.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ModuleApplication implements Validatable {
    private final ApplicationIdentifier applicationId;

    @EqualsAndHashCode.Exclude
    private final Entity application;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("applicationId", applicationId, notNull()).validate("application", application, notNull());
    }
}
