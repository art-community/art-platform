package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.platform.api.model.external.*;
import ru.art.platform.api.model.resource.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ExternalArtifactsRequest implements Validatable {
    @Singular("artifact")
    private final List<ExternalArtifact> artifacts;

    @Singular("openShiftResource")
    private final List<OpenShiftResource> openShiftResources;

    @Singular("artifactsResource")
    private final List<ArtifactsResource> artifactsResources;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("artifacts", artifacts, notEmptyCollection());
    }
}
