package ru.art.platform.api.mapping.project;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.openShift.OpenShiftLabelMapper;
import ru.art.platform.api.model.project.ProjectOpenShiftConfiguration;

public interface ProjectOpenShiftConfigurationMapper {
	String platformPodsNodeSelector = "platformPodsNodeSelector";

	ValueToModelMapper<ProjectOpenShiftConfiguration, Entity> toProjectOpenShiftConfiguration = entity -> isNotEmpty(entity) ? ProjectOpenShiftConfiguration.builder()
			.platformPodsNodeSelector(entity.getEntitySet(platformPodsNodeSelector, OpenShiftLabelMapper.toOpenShiftLabel))
			.build() : null;

	ValueFromModelMapper<ProjectOpenShiftConfiguration, Entity> fromProjectOpenShiftConfiguration = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityCollectionField(platformPodsNodeSelector, model.getPlatformPodsNodeSelector(), OpenShiftLabelMapper.fromOpenShiftLabel)
			.build() : null;
}
