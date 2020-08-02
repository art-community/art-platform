package ru.art.platform.api.model.file;

import lombok.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class StringFile {
    private final String name;
    @EqualsAndHashCode.Exclude
    private final String content;
}
