package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder=true)
@ToString(onlyExplicitlyIncluded = true)
public class UserAuthorizationRequest implements Validatable  {
    private final String name;
    private final String password;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("name", name, notEmptyString())
                .validate("password", password, notEmptyString());
    }
}
