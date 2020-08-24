package ru.art.platform.api.mapping.request;

import ru.art.entity.*;
import ru.art.entity.mapper.*;
import ru.art.generator.mapper.annotation.*;
import ru.art.platform.api.mapping.external.*;
import ru.art.platform.api.mapping.file.*;
import ru.art.platform.api.mapping.module.*;
import ru.art.platform.api.mapping.project.*;
import ru.art.platform.api.mapping.resource.*;
import ru.art.platform.api.mapping.user.*;
import ru.art.platform.api.model.request.*;
import static ru.art.core.checker.CheckerForEmptiness.*;

@IgnoreGeneration
public interface AgentModuleInstallRequestMapper {
	String projectId = "projectId";

	String module = "module";

	String openShiftResources = "openShiftResources";

	String proxyResources = "proxyResources";

	String additionalFiles = "additionalFiles";

	String configurationFiles = "configurationFiles";

	String applications = "applications";

	String user = "user";

	String notificationsConfiguration = "notificationsConfiguration";

	String probesConfiguration = "probesConfiguration";

	ValueToModelMapper<AgentModuleInstallRequest, Entity> toAgentModuleInstallRequest = entity -> isNotEmpty(entity) ? AgentModuleInstallRequest.builder()
			.projectId(entity.getValue(projectId, ExternalIdentifierMapper.toExternalIdentifier))
			.module(entity.getValue(module, ModuleMapper.toModule))
			.openShiftResources(entity.getEntitySet(openShiftResources, OpenShiftResourceMapper.toOpenShiftResource))
			.proxyResources(entity.getEntitySet(proxyResources, ProxyResourceMapper.toProxyResource))
			.additionalFiles(entity.getEntityList(additionalFiles, PlatformFileMapper.toPlatformFile))
			.configurationFiles(entity.getEntityList(configurationFiles, StringFileMapper.toStringFile))
			.applications(entity.getValue(applications, ModuleApplicationsMapper.toModuleApplications))
			.notificationsConfiguration(entity.getValue(notificationsConfiguration, ProjectNotificationsConfigurationMapper.toProjectNotificationsConfiguration))
			.user(entity.getValue(user, UserMapper.toUser))
			.probesConfiguration(entity.getValue(probesConfiguration, ProbesConfigurationMapper.toProbesConfiguration))
			.build() : null;

	ValueFromModelMapper<AgentModuleInstallRequest, Entity> fromAgentModuleInstallRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(projectId, model.getProjectId(), ExternalIdentifierMapper.fromExternalIdentifier)
			.entityField(module, model.getModule(), ModuleMapper.fromModule)
			.entityCollectionField(openShiftResources, model.getOpenShiftResources(), OpenShiftResourceMapper.fromOpenShiftResource)
			.entityCollectionField(proxyResources, model.getProxyResources(), ProxyResourceMapper.fromProxyResource)
			.entityCollectionField(additionalFiles, model.getAdditionalFiles(), PlatformFileMapper.fromPlatformFile)
			.entityCollectionField(configurationFiles, model.getConfigurationFiles(), StringFileMapper.fromStringFile)
			.entityField(applications, model.getApplications(), ModuleApplicationsMapper.fromModuleApplications)
			.entityField(notificationsConfiguration, model.getNotificationsConfiguration(), ProjectNotificationsConfigurationMapper.fromProjectNotificationsConfiguration)
			.entityField(user, model.getUser(), UserMapper.fromUser)
			.entityField(probesConfiguration, model.getProbesConfiguration(), ProbesConfigurationMapper.fromProbesConfiguration)
			.build() : null;
}
