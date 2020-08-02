package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProxyResourceRequest implements Validatable {
    private final String name;
    private final String host;
    private final Integer port;
    private final String userName;
    private final String password;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("name", name, notEmptyString()).validate("host", host, notEmptyString());
    }
}
