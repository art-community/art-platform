package ru.art.platform.common.constants;

import lombok.experimental.*;
import static ru.art.core.factory.CollectionsFactory.*;
import java.util.*;

@UtilityClass
public class GradleVersions {
    public static final String GRADLE_VERSION_6_0_1 = "6.0.1";
    public static final String GRADLE_VERSION_5_6_1 = "5.6.1";
    public static final List<String> GRADLE_VERSIONS = fixedArrayOf(GRADLE_VERSION_5_6_1, GRADLE_VERSION_6_0_1);
}
