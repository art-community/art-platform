package ru.art.platform.api.model.project;

import lombok.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProjectVersion {
    private final String reference;
    private final String version;
}
