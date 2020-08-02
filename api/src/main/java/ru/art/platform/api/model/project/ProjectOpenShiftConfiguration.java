package ru.art.platform.api.model.project;

import lombok.*;
import ru.art.platform.api.model.openShift.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProjectOpenShiftConfiguration {
    @Singular("platformPodsNodeSelectorLabel")
    private final Set<OpenShiftLabel> platformPodsNodeSelector;
}
