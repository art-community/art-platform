package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.request.ProxyResourceRequest;

public interface ProxyResourceRequestMapper {
	String name = "name";

	String host = "host";

	String port = "port";

	String userName = "userName";

	String password = "password";

	ValueToModelMapper<ProxyResourceRequest, Entity> toProxyResourceRequest = entity -> isNotEmpty(entity) ? ProxyResourceRequest.builder()
			.name(entity.getString(name))
			.host(entity.getString(host))
			.port(entity.getInt(port))
			.userName(entity.getString(userName))
			.password(entity.getString(password))
			.build() : null;

	ValueFromModelMapper<ProxyResourceRequest, Entity> fromProxyResourceRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.stringField(host, model.getHost())
			.intField(port, model.getPort())
			.stringField(userName, model.getUserName())
			.stringField(password, model.getPassword())
			.build() : null;
}
