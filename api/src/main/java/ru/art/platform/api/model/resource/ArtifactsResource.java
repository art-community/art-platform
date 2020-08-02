package ru.art.platform.api.model.resource;

import lombok.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ArtifactsResource implements Validatable {
    private final Long id;
    private final String name;
    private final String url;
    private final String userName;
    private final String password;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("name", name, notEmptyString()).validate("url", url, notEmptyString());
    }
}
