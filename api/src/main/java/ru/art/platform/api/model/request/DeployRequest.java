package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.platform.api.model.module.Module;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class DeployRequest {
    private final Module module;
}
