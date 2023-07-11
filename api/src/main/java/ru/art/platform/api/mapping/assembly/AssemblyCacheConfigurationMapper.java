package ru.art.platform.api.mapping.assembly;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.assembly.AssemblyCacheConfiguration;

public interface AssemblyCacheConfigurationMapper {
	String serverHost = "serverHost";

	String serverPort = "serverPort";

	ValueToModelMapper<AssemblyCacheConfiguration, Entity> toAssemblyCacheConfiguration = entity -> isNotEmpty(entity) ? AssemblyCacheConfiguration.builder()
			.serverHost(entity.getString(serverHost))
			.serverPort(entity.getInt(serverPort))
			.build() : null;

	ValueFromModelMapper<AssemblyCacheConfiguration, Entity> fromAssemblyCacheConfiguration = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(serverHost, model.getServerHost())
			.intField(serverPort, model.getServerPort())
			.build() : null;
}
