package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.request.FilebeatApplicationRequest;

public interface FilebeatApplicationRequestMapper {
	String name = "name";

	String url = "url";

	String resourceId = "resourceId";

	ValueToModelMapper<FilebeatApplicationRequest, Entity> toFilebeatApplicationRequest = entity -> isNotEmpty(entity) ? FilebeatApplicationRequest.builder()
			.name(entity.getString(name))
			.url(entity.getString(url))
			.resourceId(entity.getValue(resourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.build() : null;

	ValueFromModelMapper<FilebeatApplicationRequest, Entity> fromFilebeatApplicationRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.stringField(url, model.getUrl())
			.entityField(resourceId, model.getResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.build() : null;
}
