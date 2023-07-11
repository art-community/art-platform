package ru.art.platform.api.mapping.module;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.module.ModuleFilterCriteria;

public interface ModuleFilterCriteriaMapper {
	String projectIds = "projectIds";

	String states = "states";

	String ids = "ids";

	String versions = "versions";

	String sorted = "sorted";

	ValueToModelMapper<ModuleFilterCriteria, Entity> toModuleFilterCriteria = entity -> isNotEmpty(entity) ? ModuleFilterCriteria.builder()
			.projectIds(entity.getLongList(projectIds))
			.states(entity.getStringList(states))
			.ids(entity.getLongList(ids))
			.versions(entity.getStringList(versions))
			.sorted(entity.getBool(sorted))
			.build() : null;

	ValueFromModelMapper<ModuleFilterCriteria, Entity> fromModuleFilterCriteria = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longCollectionField(projectIds, model.getProjectIds())
			.stringCollectionField(states, model.getStates())
			.longCollectionField(ids, model.getIds())
			.stringCollectionField(versions, model.getVersions())
			.boolField(sorted, model.getSorted())
			.build() : null;
}
