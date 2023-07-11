package ru.art.platform.api.model.assembly;

import lombok.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class AssemblyFilterCriteria {
    private final List<Long> projectIds;
    private final List<String> states;
    private final List<String> versions;
    private final Boolean sorted;
    private final Integer count;
}
