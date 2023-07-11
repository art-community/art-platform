package ru.art.platform.api.mapping.resource;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.resource.ResourceIdentifier;

public interface ResourceIdentifierMapper {
	String id = "id";

	String name = "name";

	String type = "type";

	ValueToModelMapper<ResourceIdentifier, Entity> toResourceIdentifier = entity -> isNotEmpty(entity) ? ResourceIdentifier.builder()
			.id(entity.getLong(id))
			.name(entity.getString(name))
			.type(entity.getString(type))
			.build() : null;

	ValueFromModelMapper<ResourceIdentifier, Entity> fromResourceIdentifier = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.stringField(name, model.getName())
			.stringField(type, model.getType())
			.build() : null;
}
