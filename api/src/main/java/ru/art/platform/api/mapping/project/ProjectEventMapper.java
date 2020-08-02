package ru.art.platform.api.mapping.project;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.project.ProjectEvent;

public interface ProjectEventMapper {
	String project = "project";

	ValueToModelMapper<ProjectEvent, Entity> toProjectEvent = entity -> isNotEmpty(entity) ? ProjectEvent.builder()
			.project(entity.getValue(project, ProjectMapper.toProject))
			.build() : null;

	ValueFromModelMapper<ProjectEvent, Entity> fromProjectEvent = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(project, model.getProject(), ProjectMapper.fromProject)
			.build() : null;
}
