package ru.art.platform.api.mapping.resource;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.resource.GitResource;

public interface GitResourceMapper {
	String id = "id";

	String name = "name";

	String url = "url";

	String userName = "userName";

	String password = "password";

	ValueToModelMapper<GitResource, Entity> toGitResource = entity -> isNotEmpty(entity) ? GitResource.builder()
			.id(entity.getLong(id))
			.name(entity.getString(name))
			.url(entity.getString(url))
			.userName(entity.getString(userName))
			.password(entity.getString(password))
			.build() : null;

	ValueFromModelMapper<GitResource, Entity> fromGitResource = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.stringField(name, model.getName())
			.stringField(url, model.getUrl())
			.stringField(userName, model.getUserName())
			.stringField(password, model.getPassword())
			.build() : null;
}
