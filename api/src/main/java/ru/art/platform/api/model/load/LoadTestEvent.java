package ru.art.platform.api.model.load;

import lombok.*;
import ru.art.generator.mapper.annotation.*;

@Value
@Builder(toBuilder = true)
@IgnoreGeneration
@ToString(onlyExplicitlyIncluded = true)
public class LoadTestEvent {
    private final LoadTest loadTest;
    private final String logRecord;

    @EqualsAndHashCode.Exclude
    private final byte[] reportArchiveBytes;
}
