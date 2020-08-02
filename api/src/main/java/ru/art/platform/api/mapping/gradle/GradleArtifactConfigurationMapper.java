package ru.art.platform.api.mapping.gradle;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.gradle.GradleArtifactConfiguration;

public interface GradleArtifactConfigurationMapper {
	String arguments = "arguments";

	ValueToModelMapper<GradleArtifactConfiguration, Entity> toGradleArtifactConfiguration = entity -> isNotEmpty(entity) ? GradleArtifactConfiguration.builder()
			.arguments(entity.getString(arguments))
			.build() : null;

	ValueFromModelMapper<GradleArtifactConfiguration, Entity> fromGradleArtifactConfiguration = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(arguments, model.getArguments())
			.build() : null;
}
