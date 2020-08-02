package ru.art.platform.common.constants;

import lombok.experimental.*;

@UtilityClass
public class States {
    public static final String PROJECT_CREATED_STATE = "PROJECT_CREATED";
    public static final String PROJECT_INITIALIZED_STATE = "PROJECT_INITIALIZED";
    public static final String PROJECT_INITIALIZATION_FAILED_STATE = "PROJECT_INITIALIZATION_FAILED";
    public static final String PROJECT_RELOAD_STARTED_STATE = "PROJECT_REFRESH_STARTED";
    public static final String PROJECT_RELOADING_STATE = "PROJECT_RELOADING";

    public static final String ASSEMBLY_STARTED_STATE = "ASSEMBLY_STARTED";
    public static final String ASSEMBLY_DONE_STATE = "ASSEMBLY_DONE";
    public static final String ASSEMBLY_FAILED_STATE = "ASSEMBLY_FAILED";
    public static final String ASSEMBLY_CANCELED_STATE = "ASSEMBLY_CANCELED";
    public static final String ASSEMBLY_STARTED_ON_RESOURCE_STATE = "ASSEMBLY_STARTED_ON_RESOURCE";
    public static final String ASSEMBLY_RESTARTED_STATE = "ASSEMBLY_RESTARTED";
    public static final String ASSEMBLY_BUILDING_STATE = "ASSEMBLY_BUILDING";

    public static final String MODULE_INSTALLATION_STARTED_STATE = "MODULE_INSTALLATION_STARTED";
    public static final String MODULE_NOT_INSTALLED_STATE = "MODULE_NOT_INSTALLED";
    public static final String MODULE_INVALID_STATE = "MODULE_INVALID";
    public static final String MODULE_STOPPED_STATE = "MODULE_STOPPED";
    public static final String MODULE_RUN_STATE = "MODULE_RUN";
    public static final String MODULE_UPDATE_STARTED_STATE = "MODULE_UPDATE_STARTED";
    public static final String MODULE_STOP_STARTED_STATE = "MODULE_STOP_STARTED";
    public static final String MODULE_RESTART_STARTED_STATE = "MODULE_RESTART_STARTED";
    public static final String MODULE_UNINSTALL_STARTED_STATE = "MODULE_UNINSTALL_STARTED";
    public static final String MODULE_INSTALLING_STATE = "MODULE_INSTALLING";
    public static final String MODULE_UPDATING_STATE = "MODULE_UPDATING";
    public static final String MODULE_STOPPING_STATE = "MODULE_STOPPING";
    public static final String MODULE_RESTARTING_STATE = "MODULE_RESTARTING";
    public static final String MODULE_UNINSTALLING_STATE = "MODULE_UNINSTALLING";

    public static final String LOAD_TEST_STARTED_STATE = "LOAD_TEST_STARTED";
    public static final String LOAD_TEST_DONE_STATE = "LOAD_TEST_DONE";
    public static final String LOAD_TEST_FAILED_STATE = "LOAD_TEST_FAILED";
    public static final String LOAD_TEST_CANCELED_STATE = "LOAD_TEST_CANCELED";
    public static final String LOAD_TEST_STARTED_ON_RESOURCE_STATE = "LOAD_TEST_STARTED_ON_RESOURCE";
    public static final String LOAD_TEST_RUNNING_STATE = "LOAD_TEST_RUNNING";
}
