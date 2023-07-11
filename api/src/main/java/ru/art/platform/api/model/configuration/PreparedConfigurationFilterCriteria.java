package ru.art.platform.api.model.configuration;

import lombok.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class PreparedConfigurationFilterCriteria {
    private final Set<Long> projectIds;
    private final Set<String> profiles;
    private final Set<String> names;
}
