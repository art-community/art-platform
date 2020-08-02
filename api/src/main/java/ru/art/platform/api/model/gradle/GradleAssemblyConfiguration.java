package ru.art.platform.api.model.gradle;

import lombok.*;
import ru.art.platform.api.model.property.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class GradleAssemblyConfiguration {
    private final GradleCacheConfiguration cacheConfiguration;
    private final String arguments;
    private final String jdkVersion;
    private final String version;
    private final String initScriptGroovyContent;
    private final String initScriptKotlinContent;
    private final String initScriptFormat;
    private final List<Property> properties;
}
