package ru.art.platform.common.constants;

import lombok.experimental.*;
import static ru.art.core.factory.CollectionsFactory.*;
import java.util.*;

@UtilityClass
public class JdkVersions {
    public static final String JDK_VERSION_1_8 = "1.8";
    public static final String JDK_VERSION_11 = "11";
    public static final List<String> JDK_VERSIONS = fixedArrayOf(JDK_VERSION_1_8, JDK_VERSION_11);
}
