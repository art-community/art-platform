package ru.art.platform.api.model.module;

import lombok.*;
import java.util.List;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ModuleFilterCriteria {
    private final List<Long> projectIds;
    private final List<String> states;
    private final List<Long> ids;
    private final List<String> versions;
    private final Boolean sorted;
}
