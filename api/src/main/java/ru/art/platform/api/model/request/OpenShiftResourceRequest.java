package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.service.validation.Validatable;
import ru.art.service.validation.Validator;
import static ru.art.service.validation.ValidationExpressions.notEmptyString;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class OpenShiftResourceRequest implements Validatable  {
    private final String name;
    private final String apiUrl;
    private final String applicationsDomain;
    private final String privateRegistryUrl;
    private final String userName;
    private final String password;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("name", name, notEmptyString())
                .validate("apiUrl", apiUrl, notEmptyString())
                .validate("applicationsDomain", applicationsDomain, notEmptyString())
                .validate("privateRegistryUrl", privateRegistryUrl, notEmptyString())
                .validate("userName", userName, notEmptyString())
                .validate("password", password, notEmptyString());
    }
}
