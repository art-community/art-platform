package ru.art.platform.common.constants;

import lombok.experimental.*;

@UtilityClass
public class ErrorCodes {
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String USER_DOES_NOT_EXISTS = "USER_DOES_NOT_EXISTS";
    public static final String INVALID_PASSWORD = "INVALID_PASSWORD";
    public static final String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
    public static final String RESOURCE_ALREADY_EXISTS = "RESOURCE_ALREADY_EXISTS";
    public static final String RESOURCE_DOES_NOT_EXISTS = "RESOURCE_DOES_NOT_EXISTS";
    public static final String PREPARED_CONFIGURATION_DOES_NOT_EXISTS = "PREPARED_CONFIGURATION_DOES_NOT_EXISTS";
    public static final String PREPARED_CONFGURATION_ALREADY_EXISTS = "PREPARED_CONFGURATION_ALREADY_EXISTS";
    public static final String APPLICATION_DOES_NOT_EXISTS = "APPLICATION_DOES_NOT_EXISTS";
    public static final String PROJECT_ALREADY_EXISTS = "PROJECT_ALREADY_EXISTS";
    public static final String PROJECT_DOES_NOT_EXISTS = "PROJECT_DOES_NOT_EXISTS";
    public static final String UNKNOWN_RESOURCE_TYPE = "UNKNOWN_RESOURCE_TYPE";
    public static final String PROJECT_INITIALIZATION_ERROR = "PROJECT_INITIALIZATION_ERROR";
    public static final String LOAD_TESTING_ERROR = "LOAD_TESTING_ERROR";
    public static final String PLATFORM_ERROR = "PLATFORM_ERROR";
    public static final String OPEN_SHIFT_ERROR = "OPEN_SHIFT_ERROR";
    public static final String ASSEMBLY_DOES_NOT_EXISTS = "ASSEMBLY_DOES_NOT_EXISTS";
    public static final String BUILD_FAILED = "BUILD_FAILED";
    public static final String KANIKO_BUILD_ERROR = "KANIKO_BUILD_ERROR";
    public static final String GRADLE_BUILD_ERROR = "GRADLE_BUILD_ERROR";
    public static final String LOG_DOES_NOT_EXISTS = "LOG_DOES_NOT_EXISTS";
    public static final String MODULE_DOES_NOT_EXISTS = "MODULE_DOES_NOT_EXISTS";
    public static final String MODULE_ALREADY_EXISTS = "MODULE_ALREADY_EXISTS";
    public static final String FILE_DOES_NOT_EXISTS = "FILE_DOES_NOT_EXISTS";
    public static final String INSTALLATION_FAILED = "INSTALLATION_FAILED";
    public static final String UPDATING_FAILED = "UPDATING_FAILED";
    public static final String STOPPING_FAILED = "STOPPING_FAILED";
    public static final String RESTARTING_FAILED = "RESTARTING_FAILED";
    public static final String DELETING_FAILED = "DELETING_FAILED";
    public static final String LOAD_TEST_DOES_NOT_EXISTS = "LOAD_TEST_DOES_NOT_EXISTS";
    public static final String LOAD_TEST_SCENARIO_DOES_NOT_EXISTS = "LOAD_TEST_SCENARIO_DOES_NOT_EXISTS";
    public static final String APPLICATION_ALREADY_EXISTS = "APPLICATION_ALREADY_EXISTS";
}
