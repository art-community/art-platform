package ru.art.platform.api.mapping.configuration;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.configuration.PreparedConfigurationFilterCriteria;

public interface PreparedConfigurationFilterCriteriaMapper {
	String projectIds = "projectIds";

	String profiles = "profiles";

	String names = "names";

	ValueToModelMapper<PreparedConfigurationFilterCriteria, Entity> toPreparedConfigurationFilterCriteria = entity -> isNotEmpty(entity) ? PreparedConfigurationFilterCriteria.builder()
			.projectIds(entity.getLongSet(projectIds))
			.profiles(entity.getStringSet(profiles))
			.names(entity.getStringSet(names))
			.build() : null;

	ValueFromModelMapper<PreparedConfigurationFilterCriteria, Entity> fromPreparedConfigurationFilterCriteria = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longCollectionField(projectIds, model.getProjectIds())
			.stringCollectionField(profiles, model.getProfiles())
			.stringCollectionField(names, model.getNames())
			.build() : null;
}
