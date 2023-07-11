package ru.art.platform.api.mapping.external;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.external.ExternalArtifact;

public interface ExternalArtifactMapper {
	String name = "name";

	String version = "version";

	String projectId = "projectId";

	String externalId = "externalId";

	ValueToModelMapper<ExternalArtifact, Entity> toExternalArtifact = entity -> isNotEmpty(entity) ? ExternalArtifact.builder()
			.name(entity.getString(name))
			.version(entity.getString(version))
			.projectId(entity.getLong(projectId))
			.externalId(entity.getValue(externalId, ExternalIdentifierMapper.toExternalIdentifier))
			.build() : null;

	ValueFromModelMapper<ExternalArtifact, Entity> fromExternalArtifact = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.stringField(version, model.getVersion())
			.longField(projectId, model.getProjectId())
			.entityField(externalId, model.getExternalId(), ExternalIdentifierMapper.fromExternalIdentifier)
			.build() : null;
}
