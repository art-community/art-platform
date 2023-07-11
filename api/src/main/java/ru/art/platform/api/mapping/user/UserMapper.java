package ru.art.platform.api.mapping.user;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.generator.mapper.annotation.*;
import ru.art.platform.api.model.user.User;

@IgnoreGeneration
public interface UserMapper {
	String id = "id";

	String name = "name";

	String token = "token";

	String fullName = "fullName";

	String password = "password";

	String email = "email";

	String admin = "admin";

	String updateTimeStamp = "updateTimeStamp";

	String availableActions = "availableActions";

	String availableProjects = "availableProjects";

	ValueToModelMapper<User, Entity> toUser = entity -> isNotEmpty(entity) ? User.builder()
			.id(entity.getLong(id))
			.name(entity.getString(name))
			.token(entity.getString(token))
			.fullName(entity.getString(fullName))
			.password(entity.getCollectionValue(password).getByteArray())
			.email(entity.getString(email))
			.admin(entity.getBool(admin))
			.updateTimeStamp(entity.getLong(updateTimeStamp))
			.availableActions(entity.getStringSet(availableActions))
			.availableProjects(entity.getLongSet(availableProjects))
			.build() : null;

	ValueFromModelMapper<User, Entity> fromUser = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.stringField(name, model.getName())
			.stringField(token, model.getToken())
			.stringField(fullName, model.getFullName())
			.byteArrayField(password, model.getPassword())
			.stringField(email, model.getEmail())
			.boolField(admin, model.getAdmin())
			.longField(updateTimeStamp, model.getUpdateTimeStamp())
			.stringCollectionField(availableActions, model.getAvailableActions())
			.longCollectionField(availableProjects, model.getAvailableProjects())
			.build() : null;
}
