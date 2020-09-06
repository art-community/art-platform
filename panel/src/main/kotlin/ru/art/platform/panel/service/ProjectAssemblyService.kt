package ru.art.platform.panel.service

import org.reactivestreams.Subscription
import reactor.core.Disposable
import ru.art.core.colorizer.AnsiColorizer.error
import ru.art.logging.LoggingModule.loggingModule
import ru.art.platform.panel.agent.builder.ProjectBuilder
import ru.art.platform.api.model.assembly.*
import ru.art.platform.api.model.request.AgentProjectBuildRequest
import ru.art.platform.panel.broker.updated
import ru.art.platform.panel.client.AgentClient
import ru.art.platform.panel.client.connectToAgent
import ru.art.platform.panel.client.removeAgent
import ru.art.platform.common.constants.States.*
import ru.art.platform.common.constants.Technologies.GRADLE
import ru.art.platform.common.extensions.formatLogRecord
import ru.art.platform.panel.constants.RsocketConstants.AGENT_STREAM_CHUNKS_COUNT
import ru.art.platform.panel.extensions.ChunkedRequester
import ru.art.platform.panel.extensions.chunked
import ru.art.platform.panel.repository.ArtifactsResourceRepository.getArtifactsResources
import ru.art.platform.panel.repository.AssemblyConfigurationRepository.getAssemblyConfiguration
import ru.art.platform.panel.repository.AssemblyRepository.getAssembly
import ru.art.platform.panel.repository.AssemblyRepository.putAssembly
import ru.art.platform.panel.repository.AssemblyRepository.tryGetAssembly
import ru.art.platform.panel.repository.GitResourceRepository.getGitResource
import ru.art.platform.panel.repository.LogRepository.getLog
import ru.art.platform.panel.repository.LogRepository.putLog
import ru.art.platform.panel.repository.OpenShiftResourceRepository.getOpenShiftResources
import ru.art.platform.panel.repository.ProjectRepository.getProject
import ru.art.task.deferred.executor.SchedulerModuleActions.asynchronous
import java.time.Instant.now
import java.util.concurrent.ConcurrentHashMap

object ProjectAssemblyService {
    private val subscriptions = ConcurrentHashMap<Long, Subscription>()
    private val agents = ConcurrentHashMap<Long, AgentClient>()
    private val disposables = ConcurrentHashMap<Long, Disposable>()
    private val requester = ConcurrentHashMap<Long, ChunkedRequester>()

    fun buildProject(assembly: Assembly, artifactConfigurations: Set<ArtifactConfiguration>, builder: ProjectBuilder) {
        with(assembly) {
            val project = getProject(projectId)
            asynchronous {
                try {
                    if (getAssembly(id).isCanceled) {
                        builder.stopAgent()
                        return@asynchronous
                    }
                    val configuration = builder.startAgent()
                    if (getAssembly(id).isCanceled) {
                        builder.stopAgent()
                        return@asynchronous
                    }
                    val assemblyConfiguration = getAssemblyConfiguration(projectId)
                    val request = AgentProjectBuildRequest.builder()
                            .assembly(toBuilder().state(ASSEMBLY_STARTED_ON_RESOURCE_STATE).build().apply { updateAssembly(this) })
                            .assemblyConfiguration(assemblyConfiguration)
                            .cacheConfiguration(addAssemblyCacheServer(assemblyConfiguration, builder))
                            .projectId(project.externalId)
                            .gitResource(getGitResource(project.gitResourceId.id))
                            .openShiftResources(getOpenShiftResources())
                            .artifactsResources(getArtifactsResources())
                            .artifactConfigurations(artifactConfigurations)
                            .build()
                    disposables[id] = connectToAgent(configuration)
                            .apply { agents[id] = this }
                            .buildProject(request)
                            .chunked(requester.getOrPut(id) { ChunkedRequester(AGENT_STREAM_CHUNKS_COUNT) })
                            .doOnSubscribe { subscription -> subscriptions[id] = subscription }
                            .doOnError { error -> handleAssemblyError(error, builder) }
                            .doOnComplete { completeAssembly(builder) }
                            .subscribe { event -> updateAssembly(event) }
                } catch (error: Throwable) {
                    handleAssemblyError(error, builder)
                }
            }
        }
    }

    fun cancelAssembly(assembly: Assembly, builder: ProjectBuilder) {
        destroyAgent(assembly.id, builder)
    }

    private fun addAssemblyCacheServer(configuration: AssemblyConfiguration, builder: ProjectBuilder): AssemblyCacheConfiguration {
        when (configuration.technology) {
            GRADLE -> configuration.gradleConfiguration?.cacheConfiguration?.let {
                val agentConnectionConfiguration = builder.startCacheAgent(configuration)
                AssemblyCacheConfiguration.builder()
                        .serverHost(agentConnectionConfiguration.host())
                        .serverPort(agentConnectionConfiguration.tcpPort())
                        .build()
            }
            else -> return AssemblyCacheConfiguration.builder().build()
        }
        return AssemblyCacheConfiguration.builder().build()
    }

    private fun Assembly.updateAssembly(event: AssemblyEvent) {
        tryGetAssembly(id)
                .filter { assembly -> assembly.isRunning }
                .ifPresent { assembly ->
                    event.logRecord?.let { record ->
                        putLog(getLog(logId).toBuilder().record(formatLogRecord(record)).build()).updated()
                    }
                    if (event.assembly != assembly) {
                        putAssembly(event.assembly.toBuilder()
                                .state(ASSEMBLY_BUILDING_STATE)
                                .endTimeStamp(now().epochSecond).build())
                                .updated()
                    }
                    requester[id]?.request()
                }
    }

    private fun updateAssembly(newAssembly: Assembly) {
        tryGetAssembly(newAssembly.id)
                .filter { assembly -> assembly.isRunning }
                .ifPresent { putAssembly(newAssembly).updated() }
    }

    private fun Assembly.handleAssemblyError(error: Throwable, builder: ProjectBuilder) {
        loggingModule().getLogger(ProjectAssemblyService::class.java).error(error.message, error)
        tryGetAssembly(id)
                .filter { assembly -> assembly.isRunning }
                .ifPresent { assembly ->
                    putAssembly(assembly
                            .toBuilder()
                            .endTimeStamp(now().epochSecond)
                            .state(ASSEMBLY_FAILED_STATE)
                            .build())
                            .updated()
                    putLog(getLog(logId).toBuilder()
                            .record(formatLogRecord(error("Assembly failed: ${error.message}")))
                            .build())
                            .updated()
                }
        asynchronous { destroyAgent(id, builder) }
    }

    private fun Assembly.completeAssembly(builder: ProjectBuilder) {
        tryGetAssembly(id)
                .filter { assembly -> assembly.isRunning }
                .ifPresent { assembly ->
                    getLog(logId).updated()
                    putAssembly(assembly.toBuilder()
                            .endTimeStamp(now().epochSecond)
                            .state(ASSEMBLY_DONE_STATE)
                            .build())
                            .updated()
                }

        asynchronous { destroyAgent(id, builder) }
    }

    private fun destroyAgent(id: Long, builder: ProjectBuilder) {
        subscriptions[id]?.cancel()
        disposables[id]?.dispose()
        builder.stopAgent()
        agents[id]?.let(::removeAgent)
        agents.remove(id)
        disposables.remove(id)
        subscriptions.remove(id)
        requester.remove(id)
    }
}
