package ru.art.platform.api.mapping.file;

import ru.art.entity.*;
import ru.art.entity.mapper.*;
import ru.art.platform.api.model.file.*;
import static ru.art.core.checker.CheckerForEmptiness.*;
import static ru.art.platform.api.mapping.file.PlatformFileIdentifierMapper.*;

public interface PlatformFileChunkMapper {
    String id = "id";

    String size = "size";

    String bytes = "bytes";

    ValueToModelMapper<PlatformFileChunk, Entity> toPlatformFileChunk = entity -> isNotEmpty(entity) ? PlatformFileChunk.builder()
            .id(toPlatformFileIdentifier.map(entity.getEntity(id)))
            .size(entity.getInt(size))
            .bytes(entity.getCollectionValue(bytes).getByteArray())
            .build() : PlatformFileChunk.builder().build();

    ValueFromModelMapper<PlatformFileChunk, Entity> fromPlatformFileChunk = model -> isNotEmpty(model) ? Entity.entityBuilder()
            .entityField(id, fromPlatformFileIdentifier.map(model.getId()))
            .intField(size, model.getSize())
            .byteArrayField(bytes, model.getBytes())
            .build() : Entity.entityBuilder().build();
}
