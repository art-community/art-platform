package ru.art.platform.api.mapping.external;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.external.ExternalIdentifier;

public interface ExternalIdentifierMapper {
	String id = "id";

	String resourceId = "resourceId";

	ValueToModelMapper<ExternalIdentifier, Entity> toExternalIdentifier = entity -> isNotEmpty(entity) ? ExternalIdentifier.builder()
			.id(entity.getString(id))
			.resourceId(entity.getValue(resourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.build() : null;

	ValueFromModelMapper<ExternalIdentifier, Entity> fromExternalIdentifier = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(id, model.getId())
			.entityField(resourceId, model.getResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.build() : null;
}
