package ru.art.platform.api.model.openShift;

import lombok.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class OpenShiftPodConfiguration {
    @Singular("nodeSelectorLabel")
    private final Set<OpenShiftLabel> nodeSelector;
}
