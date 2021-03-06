package ru.art.platform.api.model.resource;

import lombok.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class PlatformResource implements Validatable {
    private final Long id;
    private final String name;
    private final String url;
    private final String password;
    private final String userName;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("name", name, notEmptyString())
                .validate("apiUrl", url, notEmptyString())
                .validate("password", password, notEmptyString())
                .validate("userName", userName, notEmptyString());
    }
}
