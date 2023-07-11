package ru.art.platform.open.shift.service

import com.openshift.internal.restclient.model.*
import com.openshift.restclient.ResourceKind.*
import com.openshift.restclient.model.*
import com.openshift.restclient.model.route.*
import org.jboss.dmr.ModelNode.*
import ru.art.logging.LoggingModule.*
import ru.art.platform.open.shift.configurator.*
import ru.art.platform.open.shift.constants.OpenShiftConstants.CLUSTER_IP
import ru.art.platform.open.shift.constants.OpenShiftConstants.NAME
import ru.art.platform.open.shift.constants.OpenShiftConstants.NODE_PORT
import ru.art.platform.open.shift.constants.OpenShiftConstants.PORT
import ru.art.platform.open.shift.constants.OpenShiftConstants.PORTS
import ru.art.platform.open.shift.constants.OpenShiftConstants.ROUTE_OPEN_SHIFT_VERSION_V_1
import ru.art.platform.open.shift.constants.OpenShiftConstants.SPEC
import ru.art.platform.open.shift.constants.OpenShiftConstants.TARGET_PORT
import ru.art.platform.open.shift.constants.OpenShiftConstants.VERSION_V_1

fun OpenShiftService.createService(name: String, project: IProject, configurator: OpenShiftServiceConfigurator.() -> OpenShiftServiceConfigurator): IService {
    val configuration = configurator(OpenShiftServiceConfigurator(OpenShiftProjectService(project, resource), name)).configuration
    return with(client.resourceFactory.create<Service>(VERSION_V_1, SERVICE, configuration.name)) {
        setNamespace(project.namespaceName)
        configuration.labels.forEach(::addLabel)
        type = configuration.type.type
        selector = configuration.podSelector
        val serviceModel = fromJSONString(toJson())
        configuration.clusterIp?.let(serviceModel.get(SPEC).get(CLUSTER_IP)::set)
        val specPorts = serviceModel.get(SPEC).get(PORTS)
        configuration.portMapping.forEach { (name, mapping) ->
            val node = specPorts.add()
            node.get(NAME).set(name)
            node.get(PORT).set(mapping.servicePort)
            node.get(TARGET_PORT).set(mapping.podPort)
            configuration.nodePortMapping[name]?.let { nodePort ->
                node.get(NODE_PORT).set(nodePort)
            }
        }
        val service = Service(serviceModel, client, emptyMap())
        loggingModule().getLogger(OpenShiftService::class.java).info("Creating service:\n${service.toJson()}")
        client.create(service)
    }
}

fun OpenShiftProjectService.createService(name: String, configurator: OpenShiftServiceConfigurator.() -> OpenShiftServiceConfigurator): IService =
        createService(name, project, configurator)


fun OpenShiftService.getService(name: String, namespace: String) =
        client.list<IService>(SERVICE, namespace).find { service -> service.name == name }

fun OpenShiftProjectService.getService(name: String) =
        getService(name, project.namespaceName)


fun OpenShiftService.deleteService(name: String, namespace: String) =
        getService(name, namespace)
                ?.let(client::delete)
                ?.let { loggingModule().getLogger(OpenShiftService::class.java).info("Deleting service '$namespace.$name'") }

fun OpenShiftProjectService.deleteService(name: String) =
        deleteService(name, project.namespaceName)


fun OpenShiftService.createRoute(name: String, service: String, project: IProject, configurator: OpenShiftRouteConfigurator.() -> OpenShiftRouteConfigurator): IRoute {
    val configuration = configurator(OpenShiftRouteConfigurator(name, service)).configuration
    return with(client.resourceFactory.create<Route>(ROUTE_OPEN_SHIFT_VERSION_V_1, ROUTE, configuration.name)) {
        setNamespace(project.namespaceName)
        configuration.labels.forEach(::addLabel)
        serviceName = configuration.service
        configuration.host?.let(::setHost)
        if (!configuration.path.isNullOrBlank()) {
            path = configuration.path
        }
        configuration.targetPort?.let { port ->
            createPort().targetPort = port
        }
        loggingModule().getLogger(OpenShiftService::class.java).info("Creating route:\n${toJson()}")
        client.create(this)
    }
}

fun OpenShiftProjectService.createRoute(name: String, service: String, configurator: OpenShiftRouteConfigurator.() -> OpenShiftRouteConfigurator): IRoute =
        createRoute(name, service, project, configurator)

fun OpenShiftService.getRoute(name: String, namespace: String) =
        client.list<IRoute>(ROUTE, namespace).find { route -> route.name == name }

fun OpenShiftProjectService.getRoute(name: String) =
        getRoute(name, project.namespaceName)


fun OpenShiftService.deleteRoute(name: String, namespace: String) =
        getRoute(name, namespace)
                ?.let(client::delete)
                ?.let { loggingModule().getLogger(OpenShiftService::class.java).info("Deleting route '$namespace.$name'") }

fun OpenShiftProjectService.deleteRoute(name: String) =
        deleteRoute(name, project.namespaceName)
