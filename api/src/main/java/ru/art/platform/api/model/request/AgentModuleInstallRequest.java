package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.generator.mapper.annotation.*;
import ru.art.platform.api.model.external.*;
import ru.art.platform.api.model.file.*;
import ru.art.platform.api.model.module.*;
import ru.art.platform.api.model.module.Module;
import ru.art.platform.api.model.project.*;
import ru.art.platform.api.model.resource.*;
import ru.art.platform.api.model.user.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
@IgnoreGeneration
public class AgentModuleInstallRequest {
    private final ExternalIdentifier projectId;
    private final Module module;
    private final User user;
    private final ProjectNotificationsConfiguration notificationsConfiguration;

    @Singular("openShiftResource")
    private final Set<OpenShiftResource> openShiftResources;

    @Singular("proxyResource")
    private final Set<ProxyResource> proxyResources;

    @Singular("additionalFile")
    private final List<PlatformFile> additionalFiles;

    @Singular("configurationFile")
    private final List<StringFile> configurationFiles;

    private ModuleApplications applications;
}
