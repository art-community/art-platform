package ru.art.platform.api.mapping.load;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.gradle.GradleAssemblyConfigurationMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.load.LoadTestScenario;

public interface LoadTestScenarioMapper {
	String id = "id";

	String projectId = "projectId";

	String name = "name";

	String defaultResourceId = "defaultResourceId";

	String launchTechnology = "launchTechnology";

	String reportTechnology = "reportTechnology";

	String gradleConfiguration = "gradleConfiguration";

	ValueToModelMapper<LoadTestScenario, Entity> toLoadTestScenario = entity -> isNotEmpty(entity) ? LoadTestScenario.builder()
			.id(entity.getLong(id))
			.projectId(entity.getLong(projectId))
			.name(entity.getString(name))
			.defaultResourceId(entity.getValue(defaultResourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.launchTechnology(entity.getString(launchTechnology))
			.reportTechnology(entity.getString(reportTechnology))
			.gradleConfiguration(entity.getValue(gradleConfiguration, GradleAssemblyConfigurationMapper.toGradleAssemblyConfiguration))
			.build() : null;

	ValueFromModelMapper<LoadTestScenario, Entity> fromLoadTestScenario = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.longField(projectId, model.getProjectId())
			.stringField(name, model.getName())
			.entityField(defaultResourceId, model.getDefaultResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.stringField(launchTechnology, model.getLaunchTechnology())
			.stringField(reportTechnology, model.getReportTechnology())
			.entityField(gradleConfiguration, model.getGradleConfiguration(), GradleAssemblyConfigurationMapper.fromGradleAssemblyConfiguration)
			.build() : null;
}
