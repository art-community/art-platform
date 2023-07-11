package ru.art.platform.agent.module

import ru.art.config.extensions.activator.AgileConfigurationsActivator.useAgileConfigurations
import ru.art.core.configurator.ModuleConfigurator
import ru.art.http.client.module.HttpClientModule
import ru.art.http.server.HttpServer.startHttpServer
import ru.art.http.server.module.HttpServerModule
import ru.art.platform.agent.cache.startCacheServer
import ru.art.platform.agent.configuration.*
import ru.art.platform.agent.specification.registerAlertManagerService
import ru.art.platform.agent.configuration.HttpServerConfiguration
import ru.art.platform.agent.configuration.ReactiveServiceConfiguration
import ru.art.platform.agent.configuration.RsocketConfiguration
import ru.art.platform.agent.configuration.ServiceConfiguration
import ru.art.platform.agent.specification.*
import ru.art.platform.common.constants.PlatformKeywords.AGENT_CAMEL_CASE
import ru.art.reactive.service.module.ReactiveServiceModule
import ru.art.rsocket.module.RsocketModule
import ru.art.rsocket.server.RsocketServer.startRsocketTcpServer
import ru.art.service.ServiceModule

object AgentModule {
    @JvmStatic
    fun main(args: Array<String>) {
        loadModules()
        registerServices()
        startCacheServer()
        startHttpServer()
        startRsocketTcpServer().await()
    }

    private fun loadModules() {
        useAgileConfigurations(AGENT_CAMEL_CASE)
                .loadModule(ReactiveServiceModule(), ReactiveServiceConfiguration())
                .loadModule(ServiceModule(), ServiceConfiguration())
                .loadModule(HttpServerModule(), ModuleConfigurator { HttpServerConfiguration() })
                .loadModule(RsocketModule(), RsocketConfiguration())
    }

    private fun registerServices() {
        registerProjectService()
        registerAssemblyService()
        registerModuleService()
        registerNetworkService()
        registerLoadTestingService()
        registerAlertManagerService()
    }
}
