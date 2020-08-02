package ru.art.platform.api.mapping.assembly;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.assembly.AssemblyFilterCriteria;

public interface AssemblyFilterCriteriaMapper {
	String projectIds = "projectIds";

	String states = "states";

	String versions = "versions";

	String sorted = "sorted";

	String count = "count";

	ValueToModelMapper<AssemblyFilterCriteria, Entity> toAssemblyFilterCriteria = entity -> isNotEmpty(entity) ? AssemblyFilterCriteria.builder()
			.projectIds(entity.getLongList(projectIds))
			.states(entity.getStringList(states))
			.versions(entity.getStringList(versions))
			.sorted(entity.getBool(sorted))
			.count(entity.getInt(count))
			.build() : null;

	ValueFromModelMapper<AssemblyFilterCriteria, Entity> fromAssemblyFilterCriteria = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longCollectionField(projectIds, model.getProjectIds())
			.stringCollectionField(states, model.getStates())
			.stringCollectionField(versions, model.getVersions())
			.boolField(sorted, model.getSorted())
			.intField(count, model.getCount())
			.build() : null;
}
