package ru.art.platform.api.model.filebeat;

import lombok.*;
import ru.art.platform.api.model.resource.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class FilebeatApplication implements Validatable {
    private final Long id;
    private final String name;
    private final String url;
    private final ResourceIdentifier resourceId;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("name", name, notEmptyString())
                .validate("url", url, notEmptyString())
                .validate("resourceId", resourceId, notNull());

        resourceId.onValidating(validator);
    }
}
