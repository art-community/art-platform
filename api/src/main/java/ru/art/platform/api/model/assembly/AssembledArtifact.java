package ru.art.platform.api.model.assembly;

import lombok.*;
import ru.art.platform.api.model.external.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class AssembledArtifact {
    private final String name;
    private final String version;
    private final ExternalIdentifier externalId;
}
