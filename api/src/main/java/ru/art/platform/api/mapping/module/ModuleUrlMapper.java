package ru.art.platform.api.mapping.module;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.module.ModuleUrl;

public interface ModuleUrlMapper {
	String url = "url";

	String port = "port";

	ValueToModelMapper<ModuleUrl, Entity> toModuleUrl = entity -> isNotEmpty(entity) ? ModuleUrl.builder()
			.url(entity.getString(url))
			.port(entity.getInt(port))
			.build() : null;

	ValueFromModelMapper<ModuleUrl, Entity> fromModuleUrl = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(url, model.getUrl())
			.intField(port, model.getPort())
			.build() : null;
}
