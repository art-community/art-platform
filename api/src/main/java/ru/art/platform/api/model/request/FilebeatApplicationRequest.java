package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.platform.api.model.resource.ResourceIdentifier;
import ru.art.service.validation.Validatable;
import ru.art.service.validation.Validator;

import static ru.art.service.validation.ValidationExpressions.notEmptyString;
import static ru.art.service.validation.ValidationExpressions.notNull;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class FilebeatApplicationRequest implements Validatable {
    private final String name;
    private final String url;
    private final ResourceIdentifier resourceId;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("name", name, notEmptyString())
                .validate("url", url, notEmptyString());
    }
}
