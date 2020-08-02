package ru.art.platform.api.model.assembly;

import lombok.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class AssemblyEvent {
    private final Assembly assembly;
    private final String logRecord;
}
