package ru.art.platform.api.mapping.file;

import ru.art.entity.*;
import ru.art.entity.mapper.*;
import ru.art.platform.api.model.file.*;
import static ru.art.core.checker.CheckerForEmptiness.*;

public interface PlatformFileMapper {
    String id = "id";

    String name = "name";

    String bytes = "bytes";

    ValueToModelMapper<PlatformFile, Entity> toPlatformFile = entity -> isNotEmpty(entity) ? PlatformFile.builder()
            .id(entity.getLong(id))
            .name(entity.getString(name))
            .bytes(entity.getCollectionValue(bytes).getByteArray())
            .build() : PlatformFile.builder().build();

    ValueFromModelMapper<PlatformFile, Entity> fromPlatformFile = model -> isNotEmpty(model) ? Entity.entityBuilder()
            .longField(id, model.getId())
            .stringField(name, model.getName())
            .byteArrayField(bytes, model.getBytes())
            .build() : Entity.entityBuilder().build();
}
