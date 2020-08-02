package ru.art.platform.open.shift.service

import com.openshift.restclient.*
import com.openshift.restclient.authorization.*
import com.openshift.restclient.model.*
import ru.art.platform.api.model.resource.*
import ru.art.platform.common.constants.ErrorCodes.*
import ru.art.platform.common.exception.*

fun <T> openShift(url: String, userName: String, password: String, action: OpenShiftService.() -> T) =
        openShift(OpenShiftResource.builder()
                .apiUrl(url)
                .password(password)
                .userName(userName)
                .build(), action)

fun <T> openShift(resource: OpenShiftResource, action: OpenShiftService.() -> T) = try {
    action(OpenShiftService(resource))
} catch (exception: UnauthorizedException) {
    val statusMessage = exception.status?.message
    val message = exception.message
    val errorMessage = statusMessage?.let { status -> message ?: status } ?: exception.javaClass.name
    throw PlatformException(OPEN_SHIFT_ERROR, errorMessage, exception)
}

class OpenShiftProjectService(internal val project: IProject, resource: OpenShiftResource) : OpenShiftService(resource)

open class OpenShiftService(internal val resource: OpenShiftResource) {
    internal val client: IClient = ClientBuilder(resource.apiUrl)
            .withUserName(resource.userName)
            .withPassword(resource.password)
            .build()
}
