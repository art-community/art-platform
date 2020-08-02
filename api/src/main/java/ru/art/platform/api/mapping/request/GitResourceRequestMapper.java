package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.request.GitResourceRequest;

public interface GitResourceRequestMapper {
	String name = "name";

	String url = "url";

	String userName = "userName";

	String password = "password";

	ValueToModelMapper<GitResourceRequest, Entity> toGitResourceRequest = entity -> isNotEmpty(entity) ? GitResourceRequest.builder()
			.name(entity.getString(name))
			.url(entity.getString(url))
			.userName(entity.getString(userName))
			.password(entity.getString(password))
			.build() : null;

	ValueFromModelMapper<GitResourceRequest, Entity> fromGitResourceRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.stringField(url, model.getUrl())
			.stringField(userName, model.getUserName())
			.stringField(password, model.getPassword())
			.build() : null;
}
