package ru.art.platform.api.mapping.assembly;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.external.ExternalIdentifierMapper;
import ru.art.platform.api.model.assembly.AssembledArtifact;

public interface AssembledArtifactMapper {
	String name = "name";

	String version = "version";

	String externalId = "externalId";

	ValueToModelMapper<AssembledArtifact, Entity> toAssembledArtifact = entity -> isNotEmpty(entity) ? AssembledArtifact.builder()
			.name(entity.getString(name))
			.version(entity.getString(version))
			.externalId(entity.getValue(externalId, ExternalIdentifierMapper.toExternalIdentifier))
			.build() : null;

	ValueFromModelMapper<AssembledArtifact, Entity> fromAssembledArtifact = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.stringField(version, model.getVersion())
			.entityField(externalId, model.getExternalId(), ExternalIdentifierMapper.fromExternalIdentifier)
			.build() : null;
}
