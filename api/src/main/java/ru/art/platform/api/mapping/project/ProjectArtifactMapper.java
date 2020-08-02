package ru.art.platform.api.mapping.project;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.project.ProjectArtifact;

public interface ProjectArtifactMapper {
	String name = "name";

	String path = "path";

	String technologies = "technologies";

	String versions = "versions";

	ValueToModelMapper<ProjectArtifact, Entity> toProjectArtifact = entity -> isNotEmpty(entity) ? ProjectArtifact.builder()
			.name(entity.getString(name))
			.path(entity.getString(path))
			.technologies(entity.getStringList(technologies))
			.versions(entity.getEntityList(versions, ProjectVersionMapper.toProjectVersion))
			.build() : null;

	ValueFromModelMapper<ProjectArtifact, Entity> fromProjectArtifact = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.stringField(path, model.getPath())
			.stringCollectionField(technologies, model.getTechnologies())
			.entityCollectionField(versions, model.getVersions(), ProjectVersionMapper.fromProjectVersion)
			.build() : null;
}
