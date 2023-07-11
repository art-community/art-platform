
package ru.art.platform.api.model.property;

import lombok.*;
import ru.art.platform.api.model.resource.*;

@Value
@Builder(toBuilder=true)
@ToString(onlyExplicitlyIncluded = true)
public class ResourceProperty {
    private final String name;
    private final String value;
    private final ResourceIdentifier resourceId;
}
