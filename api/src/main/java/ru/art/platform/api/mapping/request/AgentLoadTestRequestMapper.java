package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.external.ExternalIdentifierMapper;
import ru.art.platform.api.mapping.load.LoadTestMapper;
import ru.art.platform.api.mapping.load.LoadTestScenarioMapper;
import ru.art.platform.api.mapping.resource.GitResourceMapper;
import ru.art.platform.api.model.request.AgentLoadTestRequest;

public interface AgentLoadTestRequestMapper {
	String gitResource = "gitResource";

	String projectId = "projectId";

	String loadTest = "loadTest";

	String loadTestScenario = "loadTestScenario";

	ValueToModelMapper<AgentLoadTestRequest, Entity> toAgentLoadTestRequest = entity -> isNotEmpty(entity) ? AgentLoadTestRequest.builder()
			.gitResource(entity.getValue(gitResource, GitResourceMapper.toGitResource))
			.projectId(entity.getValue(projectId, ExternalIdentifierMapper.toExternalIdentifier))
			.loadTest(entity.getValue(loadTest, LoadTestMapper.toLoadTest))
			.loadTestScenario(entity.getValue(loadTestScenario, LoadTestScenarioMapper.toLoadTestScenario))
			.build() : null;

	ValueFromModelMapper<AgentLoadTestRequest, Entity> fromAgentLoadTestRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(gitResource, model.getGitResource(), GitResourceMapper.fromGitResource)
			.entityField(projectId, model.getProjectId(), ExternalIdentifierMapper.fromExternalIdentifier)
			.entityField(loadTest, model.getLoadTest(), LoadTestMapper.fromLoadTest)
			.entityField(loadTestScenario, model.getLoadTestScenario(), LoadTestScenarioMapper.fromLoadTestScenario)
			.build() : null;
}
