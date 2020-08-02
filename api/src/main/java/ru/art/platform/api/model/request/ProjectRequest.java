package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.platform.api.model.project.*;
import ru.art.platform.api.model.resource.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProjectRequest implements Validatable {
    private final String name;
    private final Long gitResourceId;
    private final ResourceIdentifier initializationResourceId;
    private final ProjectOpenShiftConfiguration openShiftConfiguration;
    private final ProjectNotificationsConfiguration notificationsConfiguration;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("name", name, notEmptyString())
                .validate("gitResourceId", gitResourceId, notNull())
                .validate("initializationResourceId", initializationResourceId, notNull());
        initializationResourceId.onValidating(validator);
    }
}
