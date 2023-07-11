package ru.art.platform.api.model.openShift;

import lombok.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class OpenShiftAnnotation {
    private final String name;
    private final String value;
}
