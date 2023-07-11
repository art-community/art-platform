package ru.art.platform.api.model.configuration;

import lombok.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class PreparedConfigurationRequest implements Validatable {
    private final Long projectId;
    private final String profile;
    private final String name;
    private final String configuration;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("projectId", projectId, notNull())
                .validate("profile", profile, notEmptyString())
                .validate("name", name, notEmptyString());
    }
}
