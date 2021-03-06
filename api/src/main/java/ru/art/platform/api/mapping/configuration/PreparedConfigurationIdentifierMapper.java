package ru.art.platform.api.mapping.configuration;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.configuration.PreparedConfigurationIdentifier;

public interface PreparedConfigurationIdentifierMapper {
	String id = "id";

	String projectId = "projectId";

	String profile = "profile";

	String name = "name";

	String configuration = "configuration";

	ValueToModelMapper<PreparedConfigurationIdentifier, Entity> toPreparedConfigurationIdentifier = entity -> isNotEmpty(entity) ? PreparedConfigurationIdentifier.builder()
			.id(entity.getLong(id))
			.projectId(entity.getLong(projectId))
			.profile(entity.getString(profile))
			.name(entity.getString(name))
			.configuration(entity.getString(configuration))
			.build() : null;

	ValueFromModelMapper<PreparedConfigurationIdentifier, Entity> fromPreparedConfigurationIdentifier = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.longField(projectId, model.getProjectId())
			.stringField(profile, model.getProfile())
			.stringField(name, model.getName())
			.stringField(configuration, model.getConfiguration())
			.build() : null;
}
