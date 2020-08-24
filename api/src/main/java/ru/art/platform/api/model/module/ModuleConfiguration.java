package ru.art.platform.api.model.module;

import lombok.*;
import ru.art.platform.api.model.application.*;
import ru.art.platform.api.model.assembly.*;
import ru.art.platform.api.model.configuration.*;
import ru.art.platform.api.model.file.*;
import ru.art.platform.api.model.resource.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ModuleConfiguration {
    private final ResourceIdentifier resourceId;
    private final AssembledArtifact artifact;
    private final String name;
    private final ModuleUrl url;
    private final String parameters;
    private final int count;
    private final ProbesConfiguration probesConfiguration;

    @Singular("port")
    private final List<Integer> ports;

    @Singular("preparedConfiguration")
    @EqualsAndHashCode.Exclude
    private final Set<PreparedConfigurationIdentifier> preparedConfigurations;

    @Singular("manualConfiguration")
    @EqualsAndHashCode.Exclude
    private final Set<StringFile> manualConfigurations;

    @Singular("additionalFile")
    @EqualsAndHashCode.Exclude
    private final Set<PlatformFileIdentifier> additionalFiles;

    @Singular("application")
    @EqualsAndHashCode.Exclude
    private final List<ModuleApplication> applications;
}
