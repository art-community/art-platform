package ru.art.platform.api.mapping.project;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.project.ProjectChanges;

public interface ProjectChangesMapper {
	String changedArtifacts = "changedArtifacts";

	ValueToModelMapper<ProjectChanges, Entity> toProjectChanges = entity -> isNotEmpty(entity) ? ProjectChanges.builder()
			.changedArtifacts(entity.getEntityList(changedArtifacts, ProjectArtifactMapper.toProjectArtifact))
			.build() : null;

	ValueFromModelMapper<ProjectChanges, Entity> fromProjectChanges = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityCollectionField(changedArtifacts, model.getChangedArtifacts(), ProjectArtifactMapper.fromProjectArtifact)
			.build() : null;
}
