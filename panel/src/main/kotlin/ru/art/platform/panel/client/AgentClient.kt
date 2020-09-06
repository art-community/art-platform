package ru.art.platform.panel.client

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers.elastic
import ru.art.config.extensions.ConfigExtensions.configLong
import ru.art.core.extension.ExceptionExtensions.ifExceptionOrEmpty
import ru.art.core.factory.CollectionsFactory.concurrentHashMap
import ru.art.entity.PrimitiveMapping.boolMapper
import ru.art.platform.api.constants.ApIConstants.*
import ru.art.platform.api.mapping.assembly.AssemblyEventMapper.toAssemblyEvent
import ru.art.platform.api.mapping.load.LoadTestEventMapper.toLoadTestEvent
import ru.art.platform.api.mapping.module.ModuleMapper.toModule
import ru.art.platform.api.mapping.network.NetworkAccessRequestMapper.fromNetworkAccessRequest
import ru.art.platform.api.mapping.project.ProjectChangesMapper.toProjectChanges
import ru.art.platform.api.mapping.project.ProjectEventMapper.toProjectEvent
import ru.art.platform.api.mapping.request.AgentLoadTestRequestMapper.fromAgentLoadTestRequest
import ru.art.platform.api.mapping.request.AgentModuleDeleteRequestMapper.fromAgentModuleDeleteRequest
import ru.art.platform.api.mapping.request.AgentModuleInstallRequestMapper.fromAgentModuleInstallRequest
import ru.art.platform.api.mapping.request.AgentModuleRestartRequestMapper.fromAgentModuleRestartRequest
import ru.art.platform.api.mapping.request.AgentModuleStopRequestMapper.fromAgentModuleStopRequest
import ru.art.platform.api.mapping.request.AgentModuleUpdateRequestMapper.fromAgentModuleUpdateRequest
import ru.art.platform.api.mapping.request.AgentProjectBuildRequestMapper.fromAgentProjectBuildRequest
import ru.art.platform.api.mapping.request.AgentProjectChangesRequestMapper.fromAgentProjectChangesRequest
import ru.art.platform.api.mapping.request.AgentProjectInitializationRequestMapper.fromAgentProjectInitializationRequest
import ru.art.platform.api.model.assembly.AssemblyEvent
import ru.art.platform.api.model.load.LoadTestEvent
import ru.art.platform.api.model.module.Module
import ru.art.platform.api.model.network.NetworkAccessRequest
import ru.art.platform.api.model.project.ProjectChanges
import ru.art.platform.api.model.project.ProjectEvent
import ru.art.platform.api.model.request.*
import ru.art.platform.common.constants.ErrorCodes.*
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.panel.constants.CommonConstants.DEFAULT_AGENT_TIMEOUT
import ru.art.platform.panel.constants.ConfigKeys.AGENT_TIMEOUT_MINUTES
import ru.art.rsocket.communicator.RsocketCommunicator.rsocketCommunicator
import ru.art.rsocket.model.RsocketCommunicationTargetConfiguration
import java.time.Duration.ofMinutes

private val agentClients = concurrentHashMap<Pair<String, Int>, AgentClient>()

fun connectToAgent(configuration: RsocketCommunicationTargetConfiguration): AgentClient = agentClients.getOrPut(configuration.host() to configuration.tcpPort()) {
    AgentClient(configuration)
}

fun removeAgent(client: AgentClient) = agentClients.remove(client.target.host() to client.target.tcpPort())

class AgentClient(val target: RsocketCommunicationTargetConfiguration) {
    private val communicator = rsocketCommunicator(target)
    private val agentTimeout = ofMinutes(ifExceptionOrEmpty({ configLong(AGENT_TIMEOUT_MINUTES) }, DEFAULT_AGENT_TIMEOUT))

    fun buildProject(request: AgentProjectBuildRequest): Flux<AssemblyEvent> = communicator
            .functionId(BUILD_PROJECT)
            .requestMapper(fromAgentProjectBuildRequest)
            .responseMapper(toAssemblyEvent)
            .stream<AgentProjectBuildRequest, AssemblyEvent>(request)
            .publishOn(elastic())
            .timeout(agentTimeout) { subscriber ->
                subscriber.onError(PlatformException(BUILD_FAILED, "No answer from agent during $DEFAULT_AGENT_TIMEOUT minutes"))
            }
            .map { response ->
                response.serviceException?.let { exception ->
                    throw PlatformException(exception.errorCode, response.serviceException)
                }
                response.responseData
            }

    fun initializeProject(request: AgentProjectInitializationRequest): Flux<ProjectEvent> = communicator
            .functionId(INITIALIZE_PROJECT)
            .requestMapper(fromAgentProjectInitializationRequest)
            .responseMapper(toProjectEvent)
            .stream<AgentProjectInitializationRequest, ProjectEvent>(request)
            .publishOn(elastic())
            .timeout(agentTimeout) { subscriber ->
                subscriber.onError(PlatformException(PROJECT_INITIALIZATION_ERROR, "No answer from agent during $DEFAULT_AGENT_TIMEOUT minutes"))
            }
            .map { response ->
                response.serviceException?.let { exception ->
                    throw PlatformException(exception.errorCode, response.serviceException)
                }
                return@map response.responseData
            }

    fun computeProjectChanges(request: AgentProjectChangesRequest): Mono<ProjectChanges> = communicator
            .functionId(COMPUTE_PROJECT_CHANGES)
            .requestMapper(fromAgentProjectChangesRequest)
            .responseMapper(toProjectChanges)
            .execute<AgentProjectChangesRequest, ProjectChanges>(request)
            .publishOn(elastic())
            .timeout(agentTimeout)
            .map { response ->
                response.serviceException?.let { exception ->
                    throw PlatformException(exception.errorCode, response.serviceException)
                }
                return@map response.responseData
            }

    fun installModule(request: AgentModuleInstallRequest): Flux<Module> = communicator
            .functionId(INSTALL_MODULE)
            .requestMapper(fromAgentModuleInstallRequest)
            .responseMapper(toModule)
            .stream<AgentModuleInstallRequest, Module>(request)
            .publishOn(elastic())
            .timeout(agentTimeout) { subscriber ->
                subscriber.onError(PlatformException(INSTALLATION_FAILED, "No answer from agent during $DEFAULT_AGENT_TIMEOUT minutes"))
            }
            .map { response ->
                response.serviceException?.let { exception ->
                    throw PlatformException(exception.errorCode, response.serviceException)
                }
                return@map response.responseData
            }

    fun updateModule(request: AgentModuleUpdateRequest): Flux<Module> = communicator
            .functionId(UPDATE_MODULE)
            .requestMapper(fromAgentModuleUpdateRequest)
            .responseMapper(toModule)
            .stream<AgentModuleUpdateRequest, Module>(request)
            .publishOn(elastic())
            .timeout(agentTimeout) { subscriber ->
                subscriber.onError(PlatformException(UPDATING_FAILED, "No answer from agent during $DEFAULT_AGENT_TIMEOUT minutes"))
            }
            .map { response ->
                response.serviceException?.let { exception ->
                    throw PlatformException(exception.errorCode, response.serviceException)
                }
                return@map response.responseData
            }

    fun stopModule(request: AgentModuleStopRequest): Flux<Module> = communicator
            .functionId(STOP_MODULE)
            .requestMapper(fromAgentModuleStopRequest)
            .responseMapper(toModule)
            .stream<AgentModuleStopRequest, Module>(request)
            .publishOn(elastic())
            .timeout(agentTimeout) { subscriber ->
                subscriber.onError(PlatformException(STOPPING_FAILED, "No answer from agent during $DEFAULT_AGENT_TIMEOUT minutes"))
            }
            .map { response ->
                response.serviceException?.let { exception ->
                    throw PlatformException(exception.errorCode, response.serviceException)
                }
                return@map response.responseData
            }

    fun restartModule(request: AgentModuleRestartRequest): Flux<Module> = communicator
            .functionId(RESTART_MODULE)
            .requestMapper(fromAgentModuleRestartRequest)
            .responseMapper(toModule)
            .stream<AgentModuleRestartRequest, Module>(request)
            .publishOn(elastic())
            .timeout(agentTimeout) { subscriber ->
                subscriber.onError(PlatformException(RESTARTING_FAILED, "No answer from agent during $DEFAULT_AGENT_TIMEOUT minutes"))
            }
            .map { response ->
                response.serviceException?.let { exception ->
                    throw PlatformException(exception.errorCode, response.serviceException)
                }
                return@map response.responseData
            }

    fun deleteModule(request: AgentModuleDeleteRequest): Flux<Module> = communicator
            .functionId(DELETE_MODULE)
            .requestMapper(fromAgentModuleDeleteRequest)
            .responseMapper(toModule)
            .stream<AgentModuleDeleteRequest, Module>(request)
            .publishOn(elastic())
            .timeout(agentTimeout) { subscriber ->
                subscriber.onError(PlatformException(DELETING_FAILED, "No answer from agent during $DEFAULT_AGENT_TIMEOUT minutes"))
            }
            .map { response ->
                response.serviceException?.let { exception ->
                    throw PlatformException(exception.errorCode, response.serviceException)
                }
                return@map response.responseData
            }

    fun checkNetworkAccess(request: NetworkAccessRequest): Mono<Boolean> = communicator
            .functionId(CHECK_NETWORK_ACCESS)
            .requestMapper(fromNetworkAccessRequest)
            .responseMapper(boolMapper.toModel)
            .execute<NetworkAccessRequest, Boolean>(request)
            .publishOn(elastic())
            .timeout(agentTimeout)
            .map { response -> response.responseData }

    fun startLoadTest(request: AgentLoadTestRequest): Flux<LoadTestEvent> = communicator
            .functionId(LOAD_PROJECT)
            .requestMapper(fromAgentLoadTestRequest)
            .responseMapper(toLoadTestEvent)
            .stream<AgentLoadTestRequest, LoadTestEvent>(request)
            .publishOn(elastic())
            .timeout(agentTimeout) { subscriber ->
                subscriber.onError(PlatformException(LOAD_TESTING_ERROR, "No answer from agent during $DEFAULT_AGENT_TIMEOUT minutes"))
            }
            .map { response ->
                response.serviceException?.let { exception ->
                    throw PlatformException(exception.errorCode, response.serviceException)
                }
                response.responseData
            }
}
