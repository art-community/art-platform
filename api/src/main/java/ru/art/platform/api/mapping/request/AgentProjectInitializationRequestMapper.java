package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.project.ProjectMapper;
import ru.art.platform.api.mapping.resource.GitResourceMapper;
import ru.art.platform.api.model.request.AgentProjectInitializationRequest;

public interface AgentProjectInitializationRequestMapper {
	String project = "project";

	String gitResource = "gitResource";

	ValueToModelMapper<AgentProjectInitializationRequest, Entity> toAgentProjectInitializationRequest = entity -> isNotEmpty(entity) ? AgentProjectInitializationRequest.builder()
			.project(entity.getValue(project, ProjectMapper.toProject))
			.gitResource(entity.getValue(gitResource, GitResourceMapper.toGitResource))
			.build() : null;

	ValueFromModelMapper<AgentProjectInitializationRequest, Entity> fromAgentProjectInitializationRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(project, model.getProject(), ProjectMapper.fromProject)
			.entityField(gitResource, model.getGitResource(), GitResourceMapper.fromGitResource)
			.build() : null;
}
