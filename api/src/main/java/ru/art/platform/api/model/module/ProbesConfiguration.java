package ru.art.platform.api.model.module;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProbesConfiguration {
    private final String path;
    private final boolean livenessProbe;
    private final boolean readinessProbe;
}
