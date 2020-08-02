package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import org.jose4j.jwk.*;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.generator.mapper.annotation.*;
import ru.art.platform.api.mapping.external.ExternalIdentifierMapper;
import ru.art.platform.api.mapping.module.ModuleMapper;
import ru.art.platform.api.mapping.project.ProjectNotificationsConfigurationMapper;
import ru.art.platform.api.mapping.resource.OpenShiftResourceMapper;
import ru.art.platform.api.mapping.resource.ProxyResourceMapper;
import ru.art.platform.api.mapping.user.*;
import ru.art.platform.api.model.request.AgentModuleDeleteRequest;
import ru.art.platform.api.model.user.*;

@IgnoreGeneration
public interface AgentModuleDeleteRequestMapper {
	String projectId = "projectId";

	String module = "module";

	String user = "user";

	String notificationsConfiguration = "notificationsConfiguration";

	String openShiftResources = "openShiftResources";

	String proxyResources = "proxyResources";

	ValueToModelMapper<AgentModuleDeleteRequest, Entity> toAgentModuleDeleteRequest = entity -> isNotEmpty(entity) ? AgentModuleDeleteRequest.builder()
			.projectId(entity.getValue(projectId, ExternalIdentifierMapper.toExternalIdentifier))
			.module(entity.getValue(module, ModuleMapper.toModule))
			.notificationsConfiguration(entity.getValue(notificationsConfiguration, ProjectNotificationsConfigurationMapper.toProjectNotificationsConfiguration))
			.openShiftResources(entity.getEntitySet(openShiftResources, OpenShiftResourceMapper.toOpenShiftResource))
			.proxyResources(entity.getEntitySet(proxyResources, ProxyResourceMapper.toProxyResource))
			.user(entity.getValue(user, UserMapper.toUser))
			.build() : null;

	ValueFromModelMapper<AgentModuleDeleteRequest, Entity> fromAgentModuleDeleteRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(projectId, model.getProjectId(), ExternalIdentifierMapper.fromExternalIdentifier)
			.entityField(module, model.getModule(), ModuleMapper.fromModule)
			.entityField(notificationsConfiguration, model.getNotificationsConfiguration(), ProjectNotificationsConfigurationMapper.fromProjectNotificationsConfiguration)
			.entityCollectionField(openShiftResources, model.getOpenShiftResources(), OpenShiftResourceMapper.fromOpenShiftResource)
			.entityCollectionField(proxyResources, model.getProxyResources(), ProxyResourceMapper.fromProxyResource)
			.entityField(user, model.getUser(), UserMapper.fromUser)
			.build() : null;
}
