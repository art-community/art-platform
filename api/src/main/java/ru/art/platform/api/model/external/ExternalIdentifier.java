package ru.art.platform.api.model.external;

import lombok.*;
import ru.art.platform.api.model.resource.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ExternalIdentifier {
    private final String id;
    private final ResourceIdentifier resourceId;
}
