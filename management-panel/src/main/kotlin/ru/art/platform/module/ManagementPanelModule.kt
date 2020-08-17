package ru.art.platform.module

import io.prometheus.client.hotspot.DefaultExports.register
import ru.art.config.extensions.ConfigExtensions.configBoolean
import ru.art.config.extensions.activator.AgileConfigurationsActivator.useAgileConfigurations
import ru.art.core.configurator.ModuleConfigurator
import ru.art.http.server.HttpServer.startHttpServer
import ru.art.http.server.module.HttpServerModule
import ru.art.metrics.module.MetricsModule.metricsModule
import ru.art.platform.common.constants.PlatformKeywords.PLATFORM_CAMEL_CASE
import ru.art.platform.configuration.HttpServerConfiguration
import ru.art.platform.configuration.ReactiveServiceConfiguration
import ru.art.platform.configuration.RsocketConfiguration
import ru.art.platform.configuration.ServiceConfiguration
import ru.art.platform.configurator.configureTarantool
import ru.art.platform.constants.CommonConstants.USE_LEGACY_MERGE_SORT
import ru.art.platform.service.BackupService.scheduleBackup
import ru.art.platform.service.ManagementService.handleRestart
import ru.art.platform.service.ManagementService.loadPlatformResources
import ru.art.platform.service.MigrationService.migrate
import ru.art.platform.service.SecuringService.initializeSecurity
import ru.art.platform.specification.*
import ru.art.reactive.service.module.ReactiveServiceModule
import ru.art.rsocket.module.RsocketModule
import ru.art.rsocket.server.RsocketServer.startRsocketWebSocketServer
import ru.art.service.ServiceModule
import ru.art.task.deferred.executor.SchedulerModuleActions.asynchronous
import java.lang.Boolean.TRUE
import java.lang.System.setProperty

object ManagementPanelModule {
    @JvmStatic
    fun main(args: Array<String>) {
        setProperty(USE_LEGACY_MERGE_SORT, TRUE.toString())
        initializeSecurity()
        loadModules()
        configureTarantool()
        registerRsocketServices()
        registerHttpServices()
        loadPlatformResources()
        asynchronous(::handleRestart)
        register(metricsModule().prometheusMeterRegistry.prometheusRegistry)
        scheduleBackup()
        if (configBoolean("platform", "migration.disabled", false)) {
            migrate()
        }
        startServers()
    }

    private fun loadModules() {
        useAgileConfigurations(PLATFORM_CAMEL_CASE)
                .loadModule(ReactiveServiceModule(), ReactiveServiceConfiguration())
                .loadModule(ServiceModule(), ServiceConfiguration())
                .loadModule(HttpServerModule(), ModuleConfigurator { HttpServerConfiguration() })
                .loadModule(RsocketModule(), RsocketConfiguration())
    }

    private fun registerRsocketServices() {
        registerUserService()
        registerResourceService()
        registerProjectService()
        registerAssemblyService()
        registerLogService()
        registerFileService()
        registerModuleService()
        registerNetworkAccessesService()
        registerLoadTestingService()
        registerManagementService()
        registerApplicationService()
        registerPreparedConfigurationService()
    }

    private fun registerHttpServices() {
        registerBitBucketService()
        registerBackupService()
    }

    private fun startServers() {
        startRsocketWebSocketServer()
        startHttpServer().await()
    }
}
