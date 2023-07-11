package ru.art.platform.api.mapping.filebeat;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.filebeat.FilebeatApplication;

public interface FilebeatApplicationMapper {
	String id = "id";

	String name = "name";

	String url = "url";

	String resourceId = "resourceId";

	ValueToModelMapper<FilebeatApplication, Entity> toFilebeatApplication = entity -> isNotEmpty(entity) ? FilebeatApplication.builder()
			.id(entity.getLong(id))
			.name(entity.getString(name))
			.url(entity.getString(url))
			.resourceId(entity.getValue(resourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.build() : null;

	ValueFromModelMapper<FilebeatApplication, Entity> fromFilebeatApplication = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.stringField(name, model.getName())
			.stringField(url, model.getUrl())
			.entityField(resourceId, model.getResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.build() : null;
}
