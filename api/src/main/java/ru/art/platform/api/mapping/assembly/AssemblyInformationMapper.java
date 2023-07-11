package ru.art.platform.api.mapping.assembly;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.project.ProjectVersionMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.assembly.AssemblyInformation;

public interface AssemblyInformationMapper {
	String id = "id";

	String projectId = "projectId";

	String technology = "technology";

	String version = "version";

	String state = "state";

	String startTimeStamp = "startTimeStamp";

	String endTimeStamp = "endTimeStamp";

	String resourceId = "resourceId";

	String logId = "logId";

	String artifacts = "artifacts";

	ValueToModelMapper<AssemblyInformation, Entity> toAssemblyInformation = entity -> isNotEmpty(entity) ? AssemblyInformation.builder()
			.id(entity.getLong(id))
			.projectId(entity.getLong(projectId))
			.technology(entity.getString(technology))
			.version(entity.getValue(version, ProjectVersionMapper.toProjectVersion))
			.state(entity.getString(state))
			.startTimeStamp(entity.getLong(startTimeStamp))
			.endTimeStamp(entity.getLong(endTimeStamp))
			.resourceId(entity.getValue(resourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.logId(entity.getLong(logId))
			.artifacts(entity.getEntityList(artifacts, AssembledArtifactMapper.toAssembledArtifact))
			.build() : null;

	ValueFromModelMapper<AssemblyInformation, Entity> fromAssemblyInformation = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.longField(projectId, model.getProjectId())
			.stringField(technology, model.getTechnology())
			.entityField(version, model.getVersion(), ProjectVersionMapper.fromProjectVersion)
			.stringField(state, model.getState())
			.longField(startTimeStamp, model.getStartTimeStamp())
			.longField(endTimeStamp, model.getEndTimeStamp())
			.entityField(resourceId, model.getResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.longField(logId, model.getLogId())
			.entityCollectionField(artifacts, model.getArtifacts(), AssembledArtifactMapper.fromAssembledArtifact)
			.build() : null;
}
