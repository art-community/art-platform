package ru.art.platform.api.mapping.openShift;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.openShift.OpenShiftLabel;

public interface OpenShiftLabelMapper {
	String name = "name";

	String value = "value";

	ValueToModelMapper<OpenShiftLabel, Entity> toOpenShiftLabel = entity -> isNotEmpty(entity) ? OpenShiftLabel.builder()
			.name(entity.getString(name))
			.value(entity.getString(value))
			.build() : null;

	ValueFromModelMapper<OpenShiftLabel, Entity> fromOpenShiftLabel = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.stringField(value, model.getValue())
			.build() : null;
}
