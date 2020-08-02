package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.file.PlatformFileIdentifierMapper;
import ru.art.platform.api.model.request.CloneFileRequest;

public interface CloneFileRequestMapper {
	String currentFileId = "currentFileId";

	String newFileId = "newFileId";

	ValueToModelMapper<CloneFileRequest, Entity> toCloneFileRequest = entity -> isNotEmpty(entity) ? CloneFileRequest.builder()
			.currentFileId(entity.getValue(currentFileId, PlatformFileIdentifierMapper.toPlatformFileIdentifier))
			.newFileId(entity.getValue(newFileId, PlatformFileIdentifierMapper.toPlatformFileIdentifier))
			.build() : null;

	ValueFromModelMapper<CloneFileRequest, Entity> fromCloneFileRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(currentFileId, model.getCurrentFileId(), PlatformFileIdentifierMapper.fromPlatformFileIdentifier)
			.entityField(newFileId, model.getNewFileId(), PlatformFileIdentifierMapper.fromPlatformFileIdentifier)
			.build() : null;
}
