package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.project.ProjectNotificationsConfigurationMapper;
import ru.art.platform.api.mapping.project.ProjectOpenShiftConfigurationMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.request.ProjectRequest;

public interface ProjectRequestMapper {
	String name = "name";

	String gitResourceId = "gitResourceId";

	String initializationResourceId = "initializationResourceId";

	String openShiftConfiguration = "openShiftConfiguration";

	String notificationsConfiguration = "notificationsConfiguration";

	ValueToModelMapper<ProjectRequest, Entity> toProjectRequest = entity -> isNotEmpty(entity) ? ProjectRequest.builder()
			.name(entity.getString(name))
			.gitResourceId(entity.getLong(gitResourceId))
			.initializationResourceId(entity.getValue(initializationResourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.openShiftConfiguration(entity.getValue(openShiftConfiguration, ProjectOpenShiftConfigurationMapper.toProjectOpenShiftConfiguration))
			.notificationsConfiguration(entity.getValue(notificationsConfiguration, ProjectNotificationsConfigurationMapper.toProjectNotificationsConfiguration))
			.build() : null;

	ValueFromModelMapper<ProjectRequest, Entity> fromProjectRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.longField(gitResourceId, model.getGitResourceId())
			.entityField(initializationResourceId, model.getInitializationResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.entityField(openShiftConfiguration, model.getOpenShiftConfiguration(), ProjectOpenShiftConfigurationMapper.fromProjectOpenShiftConfiguration)
			.entityField(notificationsConfiguration, model.getNotificationsConfiguration(), ProjectNotificationsConfigurationMapper.fromProjectNotificationsConfiguration)
			.build() : null;
}
