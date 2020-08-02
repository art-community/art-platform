package ru.art.platform.api.mapping.git;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.PrimitiveMapping;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.git.GitChanges;

public interface GitChangesMapper {
	String deleted = "deleted";

	String modified = "modified";

	String added = "added";

	String renamed = "renamed";

	String copied = "copied";

	ValueToModelMapper<GitChanges, Entity> toGitChanges = entity -> isNotEmpty(entity) ? GitChanges.builder()
			.deleted(entity.getStringList(deleted))
			.modified(entity.getStringList(modified))
			.added(entity.getStringList(added))
			.renamed(entity.getMap(renamed, PrimitiveMapping.StringPrimitive.toModel, PrimitiveMapping.StringPrimitive.toModel))
			.copied(entity.getMap(copied, PrimitiveMapping.StringPrimitive.toModel, PrimitiveMapping.StringPrimitive.toModel))
			.build() : null;

	ValueFromModelMapper<GitChanges, Entity> fromGitChanges = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringCollectionField(deleted, model.getDeleted())
			.stringCollectionField(modified, model.getModified())
			.stringCollectionField(added, model.getAdded())
			.mapField(renamed, model.getRenamed(), PrimitiveMapping.StringPrimitive.fromModel, PrimitiveMapping.StringPrimitive.fromModel)
			.mapField(copied, model.getCopied(), PrimitiveMapping.StringPrimitive.fromModel, PrimitiveMapping.StringPrimitive.fromModel)
			.build() : null;
}
