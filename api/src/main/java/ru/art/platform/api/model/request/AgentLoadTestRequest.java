package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.platform.api.model.external.*;
import ru.art.platform.api.model.load.*;
import ru.art.platform.api.model.resource.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class AgentLoadTestRequest implements Validatable {
    private final GitResource gitResource;
    private final ExternalIdentifier projectId;
    private final LoadTest loadTest;
    private final LoadTestScenario loadTestScenario;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("gitResource", gitResource, notNull())
                .validate("loadTest", loadTest, notNull())
                .validate("loadTestingConfiguration", loadTestScenario, notNull());
    }
}
