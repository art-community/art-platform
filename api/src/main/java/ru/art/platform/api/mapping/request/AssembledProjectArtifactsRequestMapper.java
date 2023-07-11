package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.request.AssembledProjectArtifactsRequest;

public interface AssembledProjectArtifactsRequestMapper {
	String projectId = "projectId";

	String version = "version";

	ValueToModelMapper<AssembledProjectArtifactsRequest, Entity> toAssembledProjectArtifactsRequest = entity -> isNotEmpty(entity) ? AssembledProjectArtifactsRequest.builder()
			.projectId(entity.getLong(projectId))
			.version(entity.getString(version))
			.build() : null;

	ValueFromModelMapper<AssembledProjectArtifactsRequest, Entity> fromAssembledProjectArtifactsRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(projectId, model.getProjectId())
			.stringField(version, model.getVersion())
			.build() : null;
}
