package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.platform.api.model.assembly.*;
import ru.art.platform.api.model.external.*;
import ru.art.platform.api.model.resource.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class AgentProjectBuildRequest {
    private final ExternalIdentifier projectId;
    private final Assembly assembly;
    private final AssemblyConfiguration assemblyConfiguration;
    private final AssemblyCacheConfiguration cacheConfiguration;
    private final GitResource gitResource;

    @Singular("artifactConfiguration")
    private final Set<ArtifactConfiguration> artifactConfigurations;

    @Singular("openShiftResource")
    private final Set<OpenShiftResource> openShiftResources;

    @Singular("artifactsResources")
    private final Set<ArtifactsResource> artifactsResources;
}
