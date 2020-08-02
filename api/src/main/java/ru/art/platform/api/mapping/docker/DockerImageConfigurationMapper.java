package ru.art.platform.api.mapping.docker;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.docker.DockerImageConfiguration;

public interface DockerImageConfigurationMapper {
	String image = "image";

	String containerTechnology = "containerTechnology";

	String sourcePaths = "sourcePaths";

	ValueToModelMapper<DockerImageConfiguration, Entity> toDockerImageConfiguration = entity -> isNotEmpty(entity) ? DockerImageConfiguration.builder()
			.image(entity.getString(image))
			.containerTechnology(entity.getString(containerTechnology))
			.sourcePaths(entity.getStringSet(sourcePaths))
			.build() : null;

	ValueFromModelMapper<DockerImageConfiguration, Entity> fromDockerImageConfiguration = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(image, model.getImage())
			.stringField(containerTechnology, model.getContainerTechnology())
			.stringCollectionField(sourcePaths, model.getSourcePaths())
			.build() : null;
}
