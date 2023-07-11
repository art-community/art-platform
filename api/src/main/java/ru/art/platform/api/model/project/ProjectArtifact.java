package ru.art.platform.api.model.project;

import lombok.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProjectArtifact {
    private final String name;
    private final String path;

    @Singular("technology")
    private final List<String> technologies;

    @Singular("version")
    private final List<ProjectVersion> versions;
}
