package ru.art.platform.api.model.module;

import lombok.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ModuleUrl {
    private final String url;
    private final Integer port;
}
