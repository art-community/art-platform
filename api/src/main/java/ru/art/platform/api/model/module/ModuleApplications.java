package ru.art.platform.api.model.module;

import lombok.*;
import ru.art.platform.api.model.filebeat.FilebeatModuleApplication;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ModuleApplications {
    private FilebeatModuleApplication filebeat;
}
