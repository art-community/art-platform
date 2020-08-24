package ru.art.platform.api.mapping.module;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.module.ProbesConfiguration;

public interface ProbesConfigurationMapper {
	String path = "path";

	String livenessProbe = "livenessProbe";

	String readinessProbe = "readinessProbe";

	ValueToModelMapper<ProbesConfiguration, Entity> toProbesConfiguration = entity -> isNotEmpty(entity) ? ProbesConfiguration.builder()
			.path(entity.getString(path))
			.livenessProbe(entity.getBool(livenessProbe))
			.readinessProbe(entity.getBool(readinessProbe))
			.build() : null;

	ValueFromModelMapper<ProbesConfiguration, Entity> fromProbesConfiguration = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(path, model.getPath())
			.boolField(livenessProbe, model.isLivenessProbe())
			.boolField(readinessProbe, model.isReadinessProbe())
			.build() : null;
}
