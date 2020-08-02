package ru.art.platform.api.model.log;

import lombok.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class Log {
    private final Long id;

    @Singular("record")
    @EqualsAndHashCode.Exclude
    private final List<String> records;
}
