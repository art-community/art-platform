package ru.art.platform.api.mapping.module;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.filebeat.FilebeatModuleApplicationMapper;
import ru.art.platform.api.model.module.ModuleApplications;

public interface ModuleApplicationsMapper {
	String filebeat = "filebeat";

	ValueToModelMapper<ModuleApplications, Entity> toModuleApplications = entity -> isNotEmpty(entity) ? ModuleApplications.builder()
			.filebeat(entity.getValue(filebeat, FilebeatModuleApplicationMapper.toFilebeatModuleApplication))
			.build() : null;

	ValueFromModelMapper<ModuleApplications, Entity> fromModuleApplications = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(filebeat, model.getFilebeat(), FilebeatModuleApplicationMapper.fromFilebeatModuleApplication)
			.build() : null;
}
