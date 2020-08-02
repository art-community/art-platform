package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class UpdateModulesVersionRequest implements Validatable {
    private final String version;

    @Singular("id")
    private final List<Long> ids;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("version", version, notEmptyString());
    }
}
