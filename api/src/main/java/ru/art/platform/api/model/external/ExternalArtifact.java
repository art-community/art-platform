package ru.art.platform.api.model.external;

import lombok.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ExternalArtifact {
    private final String name;
    private final String version;
    private final Long projectId;
    private final ExternalIdentifier externalId;
}
