package ru.art.platform.api.model.gradle;

import lombok.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class GradleCacheConfiguration {
    private final String serverUrlProperty;
}
