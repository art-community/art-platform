package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.platform.api.model.project.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProjectUpdateRequest implements Validatable {
    private final Long id;
    private final String name;
    private final ProjectOpenShiftConfiguration openShiftConfiguration;
    private final ProjectNotificationsConfiguration notificationsConfiguration;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("id", id, notNull())
                .validate("name", name, notEmptyString());
    }
}
