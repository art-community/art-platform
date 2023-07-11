package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.gradle.GradleAssemblyConfigurationMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.request.LoadTestScenarioRequest;

public interface LoadTestScenarioRequestMapper {
	String name = "name";

	String defaultResourceId = "defaultResourceId";

	String projectId = "projectId";

	String launchTechnology = "launchTechnology";

	String reportTechnology = "reportTechnology";

	String gradleConfiguration = "gradleConfiguration";

	ValueToModelMapper<LoadTestScenarioRequest, Entity> toLoadTestScenarioRequest = entity -> isNotEmpty(entity) ? LoadTestScenarioRequest.builder()
			.name(entity.getString(name))
			.defaultResourceId(entity.getValue(defaultResourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.projectId(entity.getLong(projectId))
			.launchTechnology(entity.getString(launchTechnology))
			.reportTechnology(entity.getString(reportTechnology))
			.gradleConfiguration(entity.getValue(gradleConfiguration, GradleAssemblyConfigurationMapper.toGradleAssemblyConfiguration))
			.build() : null;

	ValueFromModelMapper<LoadTestScenarioRequest, Entity> fromLoadTestScenarioRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.entityField(defaultResourceId, model.getDefaultResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.longField(projectId, model.getProjectId())
			.stringField(launchTechnology, model.getLaunchTechnology())
			.stringField(reportTechnology, model.getReportTechnology())
			.entityField(gradleConfiguration, model.getGradleConfiguration(), GradleAssemblyConfigurationMapper.fromGradleAssemblyConfiguration)
			.build() : null;
}
