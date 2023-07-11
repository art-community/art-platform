package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.request.ProjectChangesRequest;

public interface ProjectChangesRequestMapper {
	String projectId = "projectId";

	String reference = "reference";

	ValueToModelMapper<ProjectChangesRequest, Entity> toProjectChangesRequest = entity -> isNotEmpty(entity) ? ProjectChangesRequest.builder()
			.projectId(entity.getLong(projectId))
			.reference(entity.getString(reference))
			.build() : null;

	ValueFromModelMapper<ProjectChangesRequest, Entity> fromProjectChangesRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(projectId, model.getProjectId())
			.stringField(reference, model.getReference())
			.build() : null;
}
