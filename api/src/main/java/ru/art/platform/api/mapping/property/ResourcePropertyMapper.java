package ru.art.platform.api.mapping.property;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.property.ResourceProperty;

public interface ResourcePropertyMapper {
	String name = "name";

	String value = "value";

	String resourceId = "resourceId";

	ValueToModelMapper<ResourceProperty, Entity> toResourceProperty = entity -> isNotEmpty(entity) ? ResourceProperty.builder()
			.name(entity.getString(name))
			.value(entity.getString(value))
			.resourceId(entity.getValue(resourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.build() : null;

	ValueFromModelMapper<ResourceProperty, Entity> fromResourceProperty = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.stringField(value, model.getValue())
			.entityField(resourceId, model.getResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.build() : null;
}
