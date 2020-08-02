package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.request.UserAuthorizationRequest;

public interface UserAuthorizationRequestMapper {
	String name = "name";

	String password = "password";

	ValueToModelMapper<UserAuthorizationRequest, Entity> toUserAuthorizationRequest = entity -> isNotEmpty(entity) ? UserAuthorizationRequest.builder()
			.name(entity.getString(name))
			.password(entity.getString(password))
			.build() : null;

	ValueFromModelMapper<UserAuthorizationRequest, Entity> fromUserAuthorizationRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.stringField(password, model.getPassword())
			.build() : null;
}
