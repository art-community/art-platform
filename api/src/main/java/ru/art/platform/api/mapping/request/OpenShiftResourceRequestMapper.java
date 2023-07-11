package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.request.OpenShiftResourceRequest;

public interface OpenShiftResourceRequestMapper {
	String name = "name";

	String apiUrl = "apiUrl";

	String applicationsDomain = "applicationsDomain";

	String privateRegistryUrl = "privateRegistryUrl";

	String userName = "userName";

	String password = "password";

	ValueToModelMapper<OpenShiftResourceRequest, Entity> toOpenShiftResourceRequest = entity -> isNotEmpty(entity) ? OpenShiftResourceRequest.builder()
			.name(entity.getString(name))
			.apiUrl(entity.getString(apiUrl))
			.applicationsDomain(entity.getString(applicationsDomain))
			.privateRegistryUrl(entity.getString(privateRegistryUrl))
			.userName(entity.getString(userName))
			.password(entity.getString(password))
			.build() : null;

	ValueFromModelMapper<OpenShiftResourceRequest, Entity> fromOpenShiftResourceRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.stringField(apiUrl, model.getApiUrl())
			.stringField(applicationsDomain, model.getApplicationsDomain())
			.stringField(privateRegistryUrl, model.getPrivateRegistryUrl())
			.stringField(userName, model.getUserName())
			.stringField(password, model.getPassword())
			.build() : null;
}
