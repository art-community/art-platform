package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.module.ModuleMapper;
import ru.art.platform.api.model.request.DeployRequest;

public interface DeployRequestMapper {
	String module = "module";

	ValueToModelMapper<DeployRequest, Entity> toDeployRequest = entity -> isNotEmpty(entity) ? DeployRequest.builder()
			.module(entity.getValue(module, ModuleMapper.toModule))
			.build() : null;

	ValueFromModelMapper<DeployRequest, Entity> fromDeployRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(module, model.getModule(), ModuleMapper.fromModule)
			.build() : null;
}
