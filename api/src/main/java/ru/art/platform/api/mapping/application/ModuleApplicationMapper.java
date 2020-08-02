package ru.art.platform.api.mapping.application;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.application.ModuleApplication;

public interface ModuleApplicationMapper {
	String applicationId = "applicationId";

	String application = "application";

	ValueToModelMapper<ModuleApplication, Entity> toModuleApplication = entity -> isNotEmpty(entity) ? ModuleApplication.builder()
			.applicationId(entity.getValue(applicationId, ApplicationIdentifierMapper.toApplicationIdentifier))
			.application(entity.getEntity(application))
			.build() : null;

	ValueFromModelMapper<ModuleApplication, Entity> fromModuleApplication = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(applicationId, model.getApplicationId(), ApplicationIdentifierMapper.fromApplicationIdentifier)
			.entityField(application, model.getApplication())
			.build() : null;
}
