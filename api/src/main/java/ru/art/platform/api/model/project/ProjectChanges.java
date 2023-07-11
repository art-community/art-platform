package ru.art.platform.api.model.project;

import lombok.*;
import ru.art.platform.api.model.git.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProjectChanges {
    @EqualsAndHashCode.Exclude
    private final List<ProjectArtifact> changedArtifacts;
}
