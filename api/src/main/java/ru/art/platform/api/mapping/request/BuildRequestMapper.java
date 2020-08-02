package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.assembly.ArtifactConfigurationMapper;
import ru.art.platform.api.mapping.project.ProjectVersionMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.request.BuildRequest;

public interface BuildRequestMapper {
	String projectId = "projectId";

	String version = "version";

	String resourceId = "resourceId";

	String artifactConfigurations = "artifactConfigurations";

	ValueToModelMapper<BuildRequest, Entity> toBuildRequest = entity -> isNotEmpty(entity) ? BuildRequest.builder()
			.projectId(entity.getLong(projectId))
			.version(entity.getValue(version, ProjectVersionMapper.toProjectVersion))
			.resourceId(entity.getValue(resourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.artifactConfigurations(entity.getEntitySet(artifactConfigurations, ArtifactConfigurationMapper.toArtifactConfiguration))
			.build() : null;

	ValueFromModelMapper<BuildRequest, Entity> fromBuildRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(projectId, model.getProjectId())
			.entityField(version, model.getVersion(), ProjectVersionMapper.fromProjectVersion)
			.entityField(resourceId, model.getResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.entityCollectionField(artifactConfigurations, model.getArtifactConfigurations(), ArtifactConfigurationMapper.fromArtifactConfiguration)
			.build() : null;
}
