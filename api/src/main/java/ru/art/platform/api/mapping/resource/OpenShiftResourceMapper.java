package ru.art.platform.api.mapping.resource;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.resource.OpenShiftResource;

public interface OpenShiftResourceMapper {
	String id = "id";

	String name = "name";

	String apiUrl = "apiUrl";

	String applicationsDomain = "applicationsDomain";

	String privateRegistryUrl = "privateRegistryUrl";

	String password = "password";

	String userName = "userName";

	ValueToModelMapper<OpenShiftResource, Entity> toOpenShiftResource = entity -> isNotEmpty(entity) ? OpenShiftResource.builder()
			.id(entity.getLong(id))
			.name(entity.getString(name))
			.apiUrl(entity.getString(apiUrl))
			.applicationsDomain(entity.getString(applicationsDomain))
			.privateRegistryUrl(entity.getString(privateRegistryUrl))
			.password(entity.getString(password))
			.userName(entity.getString(userName))
			.build() : null;

	ValueFromModelMapper<OpenShiftResource, Entity> fromOpenShiftResource = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.stringField(name, model.getName())
			.stringField(apiUrl, model.getApiUrl())
			.stringField(applicationsDomain, model.getApplicationsDomain())
			.stringField(privateRegistryUrl, model.getPrivateRegistryUrl())
			.stringField(password, model.getPassword())
			.stringField(userName, model.getUserName())
			.build() : null;
}
