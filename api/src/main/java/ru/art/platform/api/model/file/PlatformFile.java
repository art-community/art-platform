package ru.art.platform.api.model.file;

import lombok.*;
import ru.art.generator.mapper.annotation.*;

@Value
@IgnoreGeneration
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class PlatformFile {
    private final Long id;
    private final String name;

    @EqualsAndHashCode.Exclude
    private final byte[] bytes;
}
