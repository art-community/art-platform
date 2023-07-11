package ru.art.platform.api.mapping.gradle;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.gradle.GradleCacheConfiguration;

public interface GradleCacheConfigurationMapper {
	String serverUrlProperty = "serverUrlProperty";

	ValueToModelMapper<GradleCacheConfiguration, Entity> toGradleCacheConfiguration = entity -> isNotEmpty(entity) ? GradleCacheConfiguration.builder()
			.serverUrlProperty(entity.getString(serverUrlProperty))
			.build() : null;

	ValueFromModelMapper<GradleCacheConfiguration, Entity> fromGradleCacheConfiguration = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(serverUrlProperty, model.getServerUrlProperty())
			.build() : null;
}
