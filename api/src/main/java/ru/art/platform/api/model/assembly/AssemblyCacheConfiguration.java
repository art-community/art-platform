package ru.art.platform.api.model.assembly;

import lombok.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class AssemblyCacheConfiguration {
    private final String serverHost;
    private final int serverPort;
}
