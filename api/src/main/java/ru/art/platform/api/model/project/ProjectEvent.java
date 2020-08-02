package ru.art.platform.api.model.project;

import lombok.*;
import ru.art.platform.api.model.assembly.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProjectEvent {
    private final Project project;
}
