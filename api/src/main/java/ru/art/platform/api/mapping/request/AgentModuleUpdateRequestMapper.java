package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.generator.mapper.annotation.*;
import ru.art.platform.api.mapping.external.ExternalIdentifierMapper;
import ru.art.platform.api.mapping.file.*;
import ru.art.platform.api.mapping.module.*;
import ru.art.platform.api.mapping.project.*;
import ru.art.platform.api.mapping.resource.*;
import ru.art.platform.api.mapping.user.*;
import ru.art.platform.api.model.request.AgentModuleUpdateRequest;

@IgnoreGeneration
public interface AgentModuleUpdateRequestMapper {
	String projectId = "projectId";

	String newModule = "newModule";

	String openShiftResources = "openShiftResources";

	String proxyResources = "proxyResources";

	String additionalFiles = "additionalFiles";

	String configurationFiles = "configurationFiles";

	String skipChangesCheck = "skipChangesCheck";

	String applications = "applications";

	String user = "user";

	String notificationsConfiguration = "notificationsConfiguration";

	String probesConfiguration = "probesConfiguration";

	ValueToModelMapper<AgentModuleUpdateRequest, Entity> toAgentModuleUpdateRequest = entity -> isNotEmpty(entity) ? AgentModuleUpdateRequest.builder()
			.projectId(entity.getValue(projectId, ExternalIdentifierMapper.toExternalIdentifier))
			.newModule(entity.getValue(newModule, ModuleMapper.toModule))
			.openShiftResources(entity.getEntitySet(openShiftResources, OpenShiftResourceMapper.toOpenShiftResource))
			.proxyResources(entity.getEntitySet(proxyResources, ProxyResourceMapper.toProxyResource))
			.additionalFiles(entity.getEntityList(additionalFiles, PlatformFileMapper.toPlatformFile))
			.configurationFiles(entity.getEntityList(configurationFiles, StringFileMapper.toStringFile))
			.skipChangesCheck(entity.getBool(skipChangesCheck))
			.applications(entity.getValue(applications, ModuleApplicationsMapper.toModuleApplications))
			.notificationsConfiguration(entity.getValue(notificationsConfiguration, ProjectNotificationsConfigurationMapper.toProjectNotificationsConfiguration))
			.user(entity.getValue(user, UserMapper.toUser))
			.probesConfiguration(entity.getValue(probesConfiguration, ProbesConfigurationMapper.toProbesConfiguration))
			.build() : null;

	ValueFromModelMapper<AgentModuleUpdateRequest, Entity> fromAgentModuleUpdateRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(projectId, model.getProjectId(), ExternalIdentifierMapper.fromExternalIdentifier)
			.entityField(newModule, model.getNewModule(), ModuleMapper.fromModule)
			.entityCollectionField(openShiftResources, model.getOpenShiftResources(), OpenShiftResourceMapper.fromOpenShiftResource)
			.entityCollectionField(proxyResources, model.getProxyResources(), ProxyResourceMapper.fromProxyResource)
			.entityCollectionField(additionalFiles, model.getAdditionalFiles(), PlatformFileMapper.fromPlatformFile)
			.entityCollectionField(configurationFiles, model.getConfigurationFiles(), StringFileMapper.fromStringFile)
			.boolField(skipChangesCheck, model.getSkipChangesCheck())
			.entityField(applications, model.getApplications(), ModuleApplicationsMapper.fromModuleApplications)
			.entityField(notificationsConfiguration, model.getNotificationsConfiguration(), ProjectNotificationsConfigurationMapper.fromProjectNotificationsConfiguration)
			.entityField(user, model.getUser(), UserMapper.fromUser)
			.entityField(probesConfiguration, model.getProbesConfiguration(), ProbesConfigurationMapper.fromProbesConfiguration)
			.build() : null;
}
