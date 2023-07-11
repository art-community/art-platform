package ru.art.platform.api.model.docker;

import lombok.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class DockerImageConfiguration {
    private final String image;
    private final String containerTechnology;
    private final Set<String> sourcePaths;
}
