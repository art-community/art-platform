package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.project.ProjectNotificationsConfigurationMapper;
import ru.art.platform.api.mapping.project.ProjectOpenShiftConfigurationMapper;
import ru.art.platform.api.model.request.ProjectUpdateRequest;

public interface ProjectUpdateRequestMapper {
	String id = "id";

	String name = "name";

	String openShiftConfiguration = "openShiftConfiguration";

	String notificationsConfiguration = "notificationsConfiguration";

	ValueToModelMapper<ProjectUpdateRequest, Entity> toProjectUpdateRequest = entity -> isNotEmpty(entity) ? ProjectUpdateRequest.builder()
			.id(entity.getLong(id))
			.name(entity.getString(name))
			.openShiftConfiguration(entity.getValue(openShiftConfiguration, ProjectOpenShiftConfigurationMapper.toProjectOpenShiftConfiguration))
			.notificationsConfiguration(entity.getValue(notificationsConfiguration, ProjectNotificationsConfigurationMapper.toProjectNotificationsConfiguration))
			.build() : null;

	ValueFromModelMapper<ProjectUpdateRequest, Entity> fromProjectUpdateRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.stringField(name, model.getName())
			.entityField(openShiftConfiguration, model.getOpenShiftConfiguration(), ProjectOpenShiftConfigurationMapper.fromProjectOpenShiftConfiguration)
			.entityField(notificationsConfiguration, model.getNotificationsConfiguration(), ProjectNotificationsConfigurationMapper.fromProjectNotificationsConfiguration)
			.build() : null;
}
