package ru.art.platform.api.model.file;

import lombok.*;
import ru.art.generator.mapper.annotation.*;

@Value
@IgnoreGeneration
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class PlatformFileChunk {
    private final int size;
    private final PlatformFileIdentifier id;

    @EqualsAndHashCode.Exclude
    private final byte[] bytes;
}
