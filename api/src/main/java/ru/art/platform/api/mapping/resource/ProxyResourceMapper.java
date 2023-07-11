package ru.art.platform.api.mapping.resource;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.resource.ProxyResource;

public interface ProxyResourceMapper {
	String id = "id";

	String name = "name";

	String host = "host";

	String port = "port";

	String userName = "userName";

	String password = "password";

	ValueToModelMapper<ProxyResource, Entity> toProxyResource = entity -> isNotEmpty(entity) ? ProxyResource.builder()
			.id(entity.getLong(id))
			.name(entity.getString(name))
			.host(entity.getString(host))
			.port(entity.getInt(port))
			.userName(entity.getString(userName))
			.password(entity.getString(password))
			.build() : null;

	ValueFromModelMapper<ProxyResource, Entity> fromProxyResource = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.stringField(name, model.getName())
			.stringField(host, model.getHost())
			.intField(port, model.getPort())
			.stringField(userName, model.getUserName())
			.stringField(password, model.getPassword())
			.build() : null;
}
