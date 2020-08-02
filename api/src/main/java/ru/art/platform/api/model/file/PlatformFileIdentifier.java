package ru.art.platform.api.model.file;

import lombok.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class PlatformFileIdentifier {
    private final Long id;
    private final String name;
}
