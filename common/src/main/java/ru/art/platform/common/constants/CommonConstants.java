package ru.art.platform.common.constants;

import lombok.experimental.*;
import java.util.*;

@UtilityClass
public class CommonConstants {
    public static final String RSOCKET_PORT_PROPERTY = "RSOCKET_PORT";
    public static final String HTTP_PORT_PROPERTY = "HTTP_PORT";
    public static final String GRADLE_CACHE_PORT_PROPERTY = "GRADLE_CACHE_PORT";
    public static final String CACHE_PORT_NAME = "cache-port";
    public static final String GRADLE_CACHE_PROPERTY = "GRADLE_CACHE";
    public static final String RSOCKET_PORT_NAME = "rsocket-port";
    public static final String HTTP_PORT_NAME = "http-port";
    public static final String HTTP_PROXY_ENVIRONMENT = "HTTP_PROXY";
    public static final int AGENT_MIN_PORT = 9000;
    public static final int AGENT_MAX_PORT = 10000;
    public static final int BROKER_PREFETCH = 256;
    public static final Locale RUSSIAN_LOCALE = new Locale("ru");
    public static final String REGEX_ANY = "\\+";
}