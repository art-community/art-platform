package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.module.ModuleConfigurationMapper;
import ru.art.platform.api.model.request.ModuleUpdateRequest;

public interface ModuleUpdateRequestMapper {
	String moduleId = "moduleId";

	String newModuleConfiguration = "newModuleConfiguration";

	ValueToModelMapper<ModuleUpdateRequest, Entity> toModuleUpdateRequest = entity -> isNotEmpty(entity) ? ModuleUpdateRequest.builder()
			.moduleId(entity.getLong(moduleId))
			.newModuleConfiguration(entity.getValue(newModuleConfiguration, ModuleConfigurationMapper.toModuleConfiguration))
			.build() : null;

	ValueFromModelMapper<ModuleUpdateRequest, Entity> fromModuleUpdateRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(moduleId, model.getModuleId())
			.entityField(newModuleConfiguration, model.getNewModuleConfiguration(), ModuleConfigurationMapper.fromModuleConfiguration)
			.build() : null;
}
