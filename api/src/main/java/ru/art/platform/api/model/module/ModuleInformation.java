package ru.art.platform.api.model.module;

import lombok.*;
import ru.art.platform.api.model.assembly.*;
import ru.art.platform.api.model.configuration.*;
import ru.art.platform.api.model.external.*;
import ru.art.platform.api.model.file.*;
import ru.art.platform.api.model.resource.*;
import static java.time.Instant.*;
import static ru.art.platform.common.constants.States.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ModuleInformation {
    private final Long id;
    private final String name;
    private final Long projectId;
    private final ExternalIdentifier externalId;
    private final String internalIp;
    private final ResourceIdentifier resourceId;
    private final AssembledArtifact artifact;
    private final ModuleUrl url;
    private final String parameters;
    private final int count;
    @Builder.Default
    private final String state = MODULE_INSTALLATION_STARTED_STATE;
    @Builder.Default
    private final Long updateTimeStamp = now().getEpochSecond();

    @Singular("port")
    private final List<Integer> ports;

    @Singular("portMapping")
    private final List<PortMapping> portMappings;

    @Singular("preparedConfiguration")
    @EqualsAndHashCode.Exclude
    private final Set<PreparedConfigurationIdentifier> preparedConfigurations;

    @Singular("manualConfiguration")
    @EqualsAndHashCode.Exclude
    private final Set<StringFile> manualConfigurations;

    @Singular("additionalFile")
    @EqualsAndHashCode.Exclude
    private final Set<PlatformFileIdentifier> additionalFiles;
}
