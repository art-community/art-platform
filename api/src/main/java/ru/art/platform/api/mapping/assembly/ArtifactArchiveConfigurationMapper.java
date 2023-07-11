package ru.art.platform.api.mapping.assembly;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.docker.DockerImageConfigurationMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.assembly.ArtifactArchiveConfiguration;

public interface ArtifactArchiveConfigurationMapper {
	String archiveTechnology = "archiveTechnology";

	String resourceId = "resourceId";

	String dockerConfiguration = "dockerConfiguration";

	ValueToModelMapper<ArtifactArchiveConfiguration, Entity> toArtifactArchiveConfiguration = entity -> isNotEmpty(entity) ? ArtifactArchiveConfiguration.builder()
			.archiveTechnology(entity.getString(archiveTechnology))
			.resourceId(entity.getValue(resourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.dockerConfiguration(entity.getValue(dockerConfiguration, DockerImageConfigurationMapper.toDockerImageConfiguration))
			.build() : null;

	ValueFromModelMapper<ArtifactArchiveConfiguration, Entity> fromArtifactArchiveConfiguration = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(archiveTechnology, model.getArchiveTechnology())
			.entityField(resourceId, model.getResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.entityField(dockerConfiguration, model.getDockerConfiguration(), DockerImageConfigurationMapper.fromDockerImageConfiguration)
			.build() : null;
}
