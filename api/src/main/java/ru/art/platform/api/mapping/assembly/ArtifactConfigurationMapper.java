package ru.art.platform.api.mapping.assembly;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.gradle.GradleArtifactConfigurationMapper;
import ru.art.platform.api.mapping.project.ProjectArtifactMapper;
import ru.art.platform.api.model.assembly.ArtifactConfiguration;

public interface ArtifactConfigurationMapper {
	String name = "name";

	String artifact = "artifact";

	String archives = "archives";

	String gradleConfiguration = "gradleConfiguration";

	ValueToModelMapper<ArtifactConfiguration, Entity> toArtifactConfiguration = entity -> isNotEmpty(entity) ? ArtifactConfiguration.builder()
			.name(entity.getString(name))
			.artifact(entity.getValue(artifact, ProjectArtifactMapper.toProjectArtifact))
			.archives(entity.getEntitySet(archives, ArtifactArchiveConfigurationMapper.toArtifactArchiveConfiguration))
			.gradleConfiguration(entity.getValue(gradleConfiguration, GradleArtifactConfigurationMapper.toGradleArtifactConfiguration))
			.build() : null;

	ValueFromModelMapper<ArtifactConfiguration, Entity> fromArtifactConfiguration = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.entityField(artifact, model.getArtifact(), ProjectArtifactMapper.fromProjectArtifact)
			.entityCollectionField(archives, model.getArchives(), ArtifactArchiveConfigurationMapper.fromArtifactArchiveConfiguration)
			.entityField(gradleConfiguration, model.getGradleConfiguration(), GradleArtifactConfigurationMapper.fromGradleArtifactConfiguration)
			.build() : null;
}
