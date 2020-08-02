package ru.art.platform.api.model.application;

import lombok.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ApplicationIdentifier implements Validatable {
    private final Long id;
    private final String name;
    private final String type;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("id", id, notNull())
                .validate("name", name, notEmptyString())
                .validate("type", type, notEmptyString());
    }
}
