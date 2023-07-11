package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.module.ModuleConfigurationMapper;
import ru.art.platform.api.model.request.ModuleInstallationRequest;

public interface ModuleInstallationRequestMapper {
	String projectId = "projectId";

	String configuration = "configuration";

	ValueToModelMapper<ModuleInstallationRequest, Entity> toModuleInstallationRequest = entity -> isNotEmpty(entity) ? ModuleInstallationRequest.builder()
			.projectId(entity.getLong(projectId))
			.configuration(entity.getValue(configuration, ModuleConfigurationMapper.toModuleConfiguration))
			.build() : null;

	ValueFromModelMapper<ModuleInstallationRequest, Entity> fromModuleInstallationRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(projectId, model.getProjectId())
			.entityField(configuration, model.getConfiguration(), ModuleConfigurationMapper.fromModuleConfiguration)
			.build() : null;
}
