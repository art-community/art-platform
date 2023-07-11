package ru.art.platform.open.shift.service

import com.openshift.internal.restclient.model.project.OpenshiftProjectRequest
import com.openshift.restclient.ResourceKind.PROJECT
import com.openshift.restclient.ResourceKind.PROJECT_REQUEST
import com.openshift.restclient.model.IProject
import ru.art.logging.LoggingModule.loggingModule
import ru.art.platform.api.model.resource.OpenShiftResource


fun IProject.toService(resource: OpenShiftResource) =
        OpenShiftProjectService(project, resource)

fun OpenShiftService.getProject(name: String): IProject? =
        client.list<IProject>(PROJECT).find { project -> project.name == name }

fun <T> OpenShiftService.getProject(name: String, handler: OpenShiftProjectService.() -> T): T? =
        client.list<IProject>(PROJECT).find { project -> project.name == name }?.let { project -> handler(OpenShiftProjectService(project, resource)) }

fun OpenShiftService.deleteProject(name: String) {
    getProject(name)?.let(client::delete)
}

fun <T> OpenShiftService.createProject(name: String, handler: OpenShiftProjectService.() -> T): T =
        handler(createProject(name))

fun OpenShiftService.createProject(name: String): OpenShiftProjectService {
    val stub = client.resourceFactory.stub<OpenshiftProjectRequest>(PROJECT_REQUEST, name)
    loggingModule().getLogger(OpenShiftService::class.java).info("Creating project:\n${stub.toJson()}")
    return OpenShiftProjectService(client.create(stub) as IProject, resource)
}
