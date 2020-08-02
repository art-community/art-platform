package ru.art.platform.api.mapping.resource;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.resource.LinuxResource;

public interface LinuxResourceMapper {
	String sshHost = "sshHost";

	String sshLogin = "sshLogin";

	String sshPassword = "sshPassword";

	ValueToModelMapper<LinuxResource, Entity> toLinuxResource = entity -> isNotEmpty(entity) ? LinuxResource.builder()
			.sshHost(entity.getString(sshHost))
			.sshLogin(entity.getString(sshLogin))
			.sshPassword(entity.getString(sshPassword))
			.build() : null;

	ValueFromModelMapper<LinuxResource, Entity> fromLinuxResource = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(sshHost, model.getSshHost())
			.stringField(sshLogin, model.getSshLogin())
			.stringField(sshPassword, model.getSshPassword())
			.build() : null;
}
