package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.request.UserRegistrationRequest;

public interface UserRegistrationRequestMapper {
	String name = "name";

	String fullName = "fullName";

	String password = "password";

	String email = "email";

	ValueToModelMapper<UserRegistrationRequest, Entity> toUserRegistrationRequest = entity -> isNotEmpty(entity) ? UserRegistrationRequest.builder()
			.name(entity.getString(name))
			.fullName(entity.getString(fullName))
			.password(entity.getString(password))
			.email(entity.getString(email))
			.build() : null;

	ValueFromModelMapper<UserRegistrationRequest, Entity> fromUserRegistrationRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.stringField(fullName, model.getFullName())
			.stringField(password, model.getPassword())
			.stringField(email, model.getEmail())
			.build() : null;
}
