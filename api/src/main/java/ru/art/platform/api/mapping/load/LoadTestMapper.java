package ru.art.platform.api.mapping.load;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.file.PlatformFileIdentifierMapper;
import ru.art.platform.api.mapping.project.ProjectVersionMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.mapping.variable.EnvironmentVariableMapper;
import ru.art.platform.api.model.load.LoadTest;

public interface LoadTestMapper {
	String id = "id";

	String projectId = "projectId";

	String scenarioId = "scenarioId";

	String version = "version";

	String startTimeStamp = "startTimeStamp";

	String endTimeStamp = "endTimeStamp";

	String state = "state";

	String resourceId = "resourceId";

	String logId = "logId";

	String reportArchiveName = "reportArchiveName";

	String environmentVariables = "environmentVariables";

	ValueToModelMapper<LoadTest, Entity> toLoadTest = entity -> isNotEmpty(entity) ? LoadTest.builder()
			.id(entity.getLong(id))
			.projectId(entity.getLong(projectId))
			.scenarioId(entity.getLong(scenarioId))
			.version(entity.getValue(version, ProjectVersionMapper.toProjectVersion))
			.startTimeStamp(entity.getLong(startTimeStamp))
			.endTimeStamp(entity.getLong(endTimeStamp))
			.state(entity.getString(state))
			.resourceId(entity.getValue(resourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.logId(entity.getLong(logId))
			.reportArchiveName(entity.getValue(reportArchiveName, PlatformFileIdentifierMapper.toPlatformFileIdentifier))
			.environmentVariables(entity.getEntityList(environmentVariables, EnvironmentVariableMapper.toEnvironmentVariable))
			.build() : null;

	ValueFromModelMapper<LoadTest, Entity> fromLoadTest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.longField(projectId, model.getProjectId())
			.longField(scenarioId, model.getScenarioId())
			.entityField(version, model.getVersion(), ProjectVersionMapper.fromProjectVersion)
			.longField(startTimeStamp, model.getStartTimeStamp())
			.longField(endTimeStamp, model.getEndTimeStamp())
			.stringField(state, model.getState())
			.entityField(resourceId, model.getResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.longField(logId, model.getLogId())
			.entityField(reportArchiveName, model.getReportArchiveName(), PlatformFileIdentifierMapper.fromPlatformFileIdentifier)
			.entityCollectionField(environmentVariables, model.getEnvironmentVariables(), EnvironmentVariableMapper.fromEnvironmentVariable)
			.build() : null;
}
