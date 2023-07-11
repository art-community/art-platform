package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder=true)
@ToString(onlyExplicitlyIncluded = true)
public class UserRegistrationRequest implements Validatable {
    private final String name;
    private final String fullName;
    private final String password;
    private final String email;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("name", name, notEmptyString())
                .validate("fullName", fullName, notEmptyString())
                .validate("email", email, notEmptyString())
                .validate("password", password, notEmptyString());
    }
}
