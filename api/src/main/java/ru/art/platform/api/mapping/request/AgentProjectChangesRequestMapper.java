package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.project.ProjectMapper;
import ru.art.platform.api.mapping.resource.GitResourceMapper;
import ru.art.platform.api.model.request.AgentProjectChangesRequest;

public interface AgentProjectChangesRequestMapper {
	String project = "project";

	String gitResource = "gitResource";

	String reference = "reference";

	String fromHash = "fromHash";

	String toHash = "toHash";

	ValueToModelMapper<AgentProjectChangesRequest, Entity> toAgentProjectChangesRequest = entity -> isNotEmpty(entity) ? AgentProjectChangesRequest.builder()
			.project(entity.getValue(project, ProjectMapper.toProject))
			.gitResource(entity.getValue(gitResource, GitResourceMapper.toGitResource))
			.reference(entity.getString(reference))
			.fromHash(entity.getString(fromHash))
			.toHash(entity.getString(toHash))
			.build() : null;

	ValueFromModelMapper<AgentProjectChangesRequest, Entity> fromAgentProjectChangesRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(project, model.getProject(), ProjectMapper.fromProject)
			.entityField(gitResource, model.getGitResource(), GitResourceMapper.fromGitResource)
			.stringField(reference, model.getReference())
			.stringField(fromHash, model.getFromHash())
			.stringField(toHash, model.getToHash())
			.build() : null;
}
