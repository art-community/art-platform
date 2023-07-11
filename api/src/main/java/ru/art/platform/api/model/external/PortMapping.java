package ru.art.platform.api.model.external;

import lombok.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class PortMapping {
    private final Integer internalPort;
    private final Integer externalPort;
}
