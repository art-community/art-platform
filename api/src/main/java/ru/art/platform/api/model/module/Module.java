package ru.art.platform.api.model.module;

import lombok.*;
import ru.art.platform.api.model.application.*;
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
public class Module {
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
    private final ProbesConfiguration probesConfiguration;

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

    @Singular("application")
    @EqualsAndHashCode.Exclude
    private final List<ModuleApplication> applications;

    public boolean isChanging() {
        switch (getState()) {
            case MODULE_NOT_INSTALLED_STATE:
            case MODULE_INVALID_STATE:
            case MODULE_STOPPED_STATE:
            case MODULE_RUN_STATE:
                return false;
            case MODULE_INSTALLATION_STARTED_STATE:
            case MODULE_UPDATE_STARTED_STATE:
            case MODULE_RESTARTING_STATE:
            case MODULE_UPDATING_STATE:
            case MODULE_INSTALLING_STATE:
            case MODULE_UNINSTALL_STARTED_STATE:
            case MODULE_RESTART_STARTED_STATE:
            case MODULE_STOP_STARTED_STATE:
            case MODULE_STOPPING_STATE:
            case MODULE_UNINSTALLING_STATE:
                return true;
        }
        return false;
    }
}
