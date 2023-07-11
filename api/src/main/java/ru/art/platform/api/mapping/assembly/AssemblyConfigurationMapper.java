package ru.art.platform.api.mapping.assembly;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.gradle.GradleAssemblyConfigurationMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.assembly.AssemblyConfiguration;

public interface AssemblyConfigurationMapper {
	String id = "id";

	String defaultResourceId = "defaultResourceId";

	String technology = "technology";

	String gradleConfiguration = "gradleConfiguration";

	String artifactConfigurations = "artifactConfigurations";

	ValueToModelMapper<AssemblyConfiguration, Entity> toAssemblyConfiguration = entity -> isNotEmpty(entity) ? AssemblyConfiguration.builder()
			.id(entity.getLong(id))
			.defaultResourceId(entity.getValue(defaultResourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.technology(entity.getString(technology))
			.gradleConfiguration(entity.getValue(gradleConfiguration, GradleAssemblyConfigurationMapper.toGradleAssemblyConfiguration))
			.artifactConfigurations(entity.getEntityList(artifactConfigurations, ArtifactConfigurationMapper.toArtifactConfiguration))
			.build() : null;

	ValueFromModelMapper<AssemblyConfiguration, Entity> fromAssemblyConfiguration = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.entityField(defaultResourceId, model.getDefaultResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.stringField(technology, model.getTechnology())
			.entityField(gradleConfiguration, model.getGradleConfiguration(), GradleAssemblyConfigurationMapper.fromGradleAssemblyConfiguration)
			.entityCollectionField(artifactConfigurations, model.getArtifactConfigurations(), ArtifactConfigurationMapper.fromArtifactConfiguration)
			.build() : null;
}
