package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.project.ProjectVersionMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.mapping.variable.EnvironmentVariableMapper;
import ru.art.platform.api.model.request.LoadTestRequest;

public interface LoadTestRequestMapper {
	String scenarioId = "scenarioId";

	String resourceId = "resourceId";

	String projectId = "projectId";

	String version = "version";

	String environmentVariables = "environmentVariables";

	ValueToModelMapper<LoadTestRequest, Entity> toLoadTestRequest = entity -> isNotEmpty(entity) ? LoadTestRequest.builder()
			.scenarioId(entity.getLong(scenarioId))
			.resourceId(entity.getValue(resourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.projectId(entity.getLong(projectId))
			.version(entity.getValue(version, ProjectVersionMapper.toProjectVersion))
			.environmentVariables(entity.getEntityList(environmentVariables, EnvironmentVariableMapper.toEnvironmentVariable))
			.build() : null;

	ValueFromModelMapper<LoadTestRequest, Entity> fromLoadTestRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(scenarioId, model.getScenarioId())
			.entityField(resourceId, model.getResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.longField(projectId, model.getProjectId())
			.entityField(version, model.getVersion(), ProjectVersionMapper.fromProjectVersion)
			.entityCollectionField(environmentVariables, model.getEnvironmentVariables(), EnvironmentVariableMapper.fromEnvironmentVariable)
			.build() : null;
}
