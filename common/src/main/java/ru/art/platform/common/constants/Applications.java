package ru.art.platform.common.constants;

import lombok.experimental.*;
import static ru.art.core.factory.CollectionsFactory.*;
import java.util.*;

@UtilityClass
public class Applications {
    public final String FILEBEAT_APPLICATION = "FILEBEAT_APPLICATION";
    public final String FILEBEAT_CONFIG = "filebeat-config";
    public final String FILEBEAT_DATA = "filebeat-data";
    public final String FILEBEAT = "filebeat";
    public final String FILEBEAT_CONFIG_FILE = "filebeat.yml";

    public final List<String> APPLICATION_TYPES = fixedArrayOf(FILEBEAT);
}
