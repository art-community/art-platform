package ru.art.platform.api.mapping.openShift;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.openShift.OpenShiftPodConfiguration;

public interface OpenShiftPodConfigurationMapper {
	String nodeSelector = "nodeSelector";

	ValueToModelMapper<OpenShiftPodConfiguration, Entity> toOpenShiftPodConfiguration = entity -> isNotEmpty(entity) ? OpenShiftPodConfiguration.builder()
			.nodeSelector(entity.getEntitySet(nodeSelector, OpenShiftLabelMapper.toOpenShiftLabel))
			.build() : null;

	ValueFromModelMapper<OpenShiftPodConfiguration, Entity> fromOpenShiftPodConfiguration = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityCollectionField(nodeSelector, model.getNodeSelector(), OpenShiftLabelMapper.fromOpenShiftLabel)
			.build() : null;
}
