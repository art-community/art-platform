package ru.art.platform.api.mapping.project;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.project.ProjectVersion;

public interface ProjectVersionMapper {
	String reference = "reference";

	String version = "version";

	ValueToModelMapper<ProjectVersion, Entity> toProjectVersion = entity -> isNotEmpty(entity) ? ProjectVersion.builder()
			.reference(entity.getString(reference))
			.version(entity.getString(version))
			.build() : null;

	ValueFromModelMapper<ProjectVersion, Entity> fromProjectVersion = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(reference, model.getReference())
			.stringField(version, model.getVersion())
			.build() : null;
}
