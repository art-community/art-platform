package ru.art.platform.api.mapping.resource;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.resource.PlatformResource;

public interface PlatformResourceMapper {
	String id = "id";

	String name = "name";

	String url = "url";

	String password = "password";

	String userName = "userName";

	ValueToModelMapper<PlatformResource, Entity> toPlatformResource = entity -> isNotEmpty(entity) ? PlatformResource.builder()
			.id(entity.getLong(id))
			.name(entity.getString(name))
			.url(entity.getString(url))
			.password(entity.getString(password))
			.userName(entity.getString(userName))
			.build() : null;

	ValueFromModelMapper<PlatformResource, Entity> fromPlatformResource = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.stringField(name, model.getName())
			.stringField(url, model.getUrl())
			.stringField(password, model.getPassword())
			.stringField(userName, model.getUserName())
			.build() : null;
}
