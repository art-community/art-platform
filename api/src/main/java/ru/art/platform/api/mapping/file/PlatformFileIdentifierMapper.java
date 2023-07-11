package ru.art.platform.api.mapping.file;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.file.PlatformFileIdentifier;

public interface PlatformFileIdentifierMapper {
	String id = "id";

	String name = "name";

	ValueToModelMapper<PlatformFileIdentifier, Entity> toPlatformFileIdentifier = entity -> isNotEmpty(entity) ? PlatformFileIdentifier.builder()
			.id(entity.getLong(id))
			.name(entity.getString(name))
			.build() : null;

	ValueFromModelMapper<PlatformFileIdentifier, Entity> fromPlatformFileIdentifier = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.stringField(name, model.getName())
			.build() : null;
}
