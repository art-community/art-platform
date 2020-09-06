package ru.art.platform.panel.service

import reactor.core.publisher.Flux
import ru.art.platform.panel.agent.builder.createProjectBuilder
import ru.art.platform.api.model.assembly.*
import ru.art.platform.api.model.log.Log
import ru.art.platform.api.model.request.BuildRequest
import ru.art.platform.panel.broker.added
import ru.art.platform.panel.broker.assemblyConsumer
import ru.art.platform.panel.broker.deleted
import ru.art.platform.panel.broker.updated
import ru.art.platform.common.broker.PlatformEvent
import ru.art.platform.common.constants.States.*
import ru.art.platform.panel.factory.AssemblyFactory.createAssembly
import ru.art.platform.panel.filter.AssemblyFilter
import ru.art.platform.panel.repository.AssemblyRepository
import ru.art.platform.panel.repository.AssemblyRepository.getAssemblies
import ru.art.platform.panel.repository.AssemblyRepository.getAssembly
import ru.art.platform.panel.repository.AssemblyRepository.getProjectAssemblies
import ru.art.platform.panel.repository.AssemblyRepository.putAssembly
import ru.art.platform.panel.repository.LogRepository.putLog
import ru.art.platform.panel.repository.ProjectRepository.getProject
import ru.art.platform.panel.service.ProjectAssemblyService.buildProject
import ru.art.platform.panel.service.ProjectAssemblyService.cancelAssembly
import ru.art.task.deferred.executor.SchedulerModuleActions.asynchronous
import java.time.Instant.now

object AssemblyService {
    fun buildProject(request: BuildRequest): Assembly {
        getRunningAssemblies(request.version.version, request.projectId, request.artifactConfigurations)
                .parallelStream()
                .forEach { assembly -> cancelAssembly(assembly.id) }
        val assembly = putAssembly(createAssembly(request)).added()
        val builder = createProjectBuilder(request.resourceId, assembly, getProject(assembly.projectId))
        buildProject(assembly, request.artifactConfigurations, builder)
        return assembly
    }

    fun cancelAssembly(id: Long): Assembly {
        val currentAssembly = getAssembly(id)
        if (!currentAssembly.isRunning) {
            return currentAssembly
        }
        val newAssembly = putAssembly(currentAssembly
                .toBuilder()
                .state(ASSEMBLY_CANCELED_STATE)
                .endTimeStamp(now().epochSecond)
                .build())
                .updated()
        if (currentAssembly.state != ASSEMBLY_BUILDING_STATE && currentAssembly.state != ASSEMBLY_STARTED_ON_RESOURCE_STATE) {
            return newAssembly
        }
        asynchronous {
            val builder = createProjectBuilder(newAssembly.resourceId, newAssembly, getProject(newAssembly.projectId))
            cancelAssembly(newAssembly, builder)
        }
        return newAssembly
    }

    fun rebuildProject(assemblyId: Long) {
        var assembly = getAssembly(assemblyId)
        if (assembly.isRunning) {
            cancelAssembly(assembly.id)
        }
        assembly = putAssembly(assembly.toBuilder()
                .startTimeStamp(now().epochSecond)
                .state(ASSEMBLY_RESTARTED_STATE)
                .logId(putLog(Log.builder().build()).id)
                .clearArtifacts()
                .build())
                .updated()
        val builder = createProjectBuilder(assembly.resourceId, assembly, getProject(assembly.projectId))
        buildProject(assembly, assembly.artifactConfigurations, builder)
    }

    fun subscribeOnAssembly(): Flux<PlatformEvent> = assemblyConsumer()

    fun getRunningAssemblies(version: String, projectId: Long, artifacts: Set<ArtifactConfiguration>): List<Assembly> = getRunningAssemblies()
            .filter { assembly ->
                assembly.version.version == version
                        && assembly.projectId == projectId
                        && assembly.artifactConfigurations.all(artifacts::contains)
            }

    fun getRunningAssemblies(): List<Assembly> = getAssemblies().filter(Assembly::isRunning)

    fun getFilteredAssemblies(filterCriteria: AssemblyFilterCriteria): List<AssemblyInformation> = AssemblyFilter(filterCriteria).filter()

    fun stopRunningAssemblies() {
        getRunningAssemblies()
                .map(Assembly::getId)
                .forEach { id -> cancelAssembly(id) }
    }

    fun getLatestAssembledArtifacts(projectId: Long): Set<AssembledArtifact> =
            getProjectAssemblies(projectId)
                    .filter { assembly -> assembly.state == ASSEMBLY_DONE_STATE }
                    .maxBy { assembly -> assembly.endTimeStamp }!!
                    .artifacts

    fun deleteProjectAssemblies(projectId: Long) {
        getProjectAssemblies(projectId).forEach { assembly ->
            cancelAssembly(assembly.id)
            deleteAssembly(assembly.id)
        }
    }

    fun deleteAssembly(id: Long) = AssemblyRepository.deleteAssembly(id).deleted()
}
