package ru.art.platform.api.model.configuration;

import lombok.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class PreparedConfigurationIdentifier {
    private final Long id;
    private final Long projectId;
    private final String profile;
    private final String name;
    private final String configuration;
}
