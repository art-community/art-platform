package ru.art.platform.api.mapping.assembly;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.project.ProjectVersionMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.assembly.Assembly;

public interface AssemblyMapper {
	String id = "id";

	String projectId = "projectId";

	String technology = "technology";

	String version = "version";

	String resourceId = "resourceId";

	String logId = "logId";

	String endTimeStamp = "endTimeStamp";

	String startTimeStamp = "startTimeStamp";

	String state = "state";

	String artifacts = "artifacts";

	String artifactConfigurations = "artifactConfigurations";

	ValueToModelMapper<Assembly, Entity> toAssembly = entity -> isNotEmpty(entity) ? Assembly.builder()
			.id(entity.getLong(id))
			.projectId(entity.getLong(projectId))
			.technology(entity.getString(technology))
			.version(entity.getValue(version, ProjectVersionMapper.toProjectVersion))
			.resourceId(entity.getValue(resourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.logId(entity.getLong(logId))
			.endTimeStamp(entity.getLong(endTimeStamp))
			.startTimeStamp(entity.getLong(startTimeStamp))
			.state(entity.getString(state))
			.artifacts(entity.getEntitySet(artifacts, AssembledArtifactMapper.toAssembledArtifact))
			.artifactConfigurations(entity.getEntitySet(artifactConfigurations, ArtifactConfigurationMapper.toArtifactConfiguration))
			.build() : null;

	ValueFromModelMapper<Assembly, Entity> fromAssembly = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.longField(projectId, model.getProjectId())
			.stringField(technology, model.getTechnology())
			.entityField(version, model.getVersion(), ProjectVersionMapper.fromProjectVersion)
			.entityField(resourceId, model.getResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.longField(logId, model.getLogId())
			.longField(endTimeStamp, model.getEndTimeStamp())
			.longField(startTimeStamp, model.getStartTimeStamp())
			.stringField(state, model.getState())
			.entityCollectionField(artifacts, model.getArtifacts(), AssembledArtifactMapper.fromAssembledArtifact)
			.entityCollectionField(artifactConfigurations, model.getArtifactConfigurations(), ArtifactConfigurationMapper.fromArtifactConfiguration)
			.build() : null;
}
