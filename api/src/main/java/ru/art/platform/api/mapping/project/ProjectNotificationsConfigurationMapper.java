package ru.art.platform.api.mapping.project;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.project.ProjectNotificationsConfiguration;

public interface ProjectNotificationsConfigurationMapper {
	String url = "url";

	String additionalMessage = "additionalMessage";

	String proxyId = "proxyId";

	ValueToModelMapper<ProjectNotificationsConfiguration, Entity> toProjectNotificationsConfiguration = entity -> isNotEmpty(entity) ? ProjectNotificationsConfiguration.builder()
			.url(entity.getString(url))
			.additionalMessage(entity.getString(additionalMessage))
			.proxyId(entity.getValue(proxyId, ResourceIdentifierMapper.toResourceIdentifier))
			.build() : null;

	ValueFromModelMapper<ProjectNotificationsConfiguration, Entity> fromProjectNotificationsConfiguration = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(url, model.getUrl())
			.stringField(additionalMessage, model.getAdditionalMessage())
			.entityField(proxyId, model.getProxyId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.build() : null;
}
