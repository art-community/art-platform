package ru.art.platform.api.mapping.filebeat;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.application.ApplicationIdentifierMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.filebeat.FilebeatModuleApplication;

public interface FilebeatModuleApplicationMapper {
	String applicationId = "applicationId";

	String url = "url";

	String resourceId = "resourceId";

	String port = "port";

	String configuration = "configuration";

	ValueToModelMapper<FilebeatModuleApplication, Entity> toFilebeatModuleApplication = entity -> isNotEmpty(entity) ? FilebeatModuleApplication.builder()
			.applicationId(entity.getValue(applicationId, ApplicationIdentifierMapper.toApplicationIdentifier))
			.url(entity.getString(url))
			.resourceId(entity.getValue(resourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.port(entity.getInt(port))
			.configuration(entity.getString(configuration))
			.build() : null;

	ValueFromModelMapper<FilebeatModuleApplication, Entity> fromFilebeatModuleApplication = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(applicationId, model.getApplicationId(), ApplicationIdentifierMapper.fromApplicationIdentifier)
			.stringField(url, model.getUrl())
			.entityField(resourceId, model.getResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.intField(port, model.getPort())
			.stringField(configuration, model.getConfiguration())
			.build() : null;
}
