package ru.art.platform.api.model.variable;

import lombok.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class EnvironmentVariable {
    private final String name;
    private final String value;
}
