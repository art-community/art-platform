package ru.art.platform.api.mapping.openShift;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.openShift.OpenShiftAnnotation;

public interface OpenShiftAnnotationMapper {
	String name = "name";

	String value = "value";

	ValueToModelMapper<OpenShiftAnnotation, Entity> toOpenShiftAnnotation = entity -> isNotEmpty(entity) ? OpenShiftAnnotation.builder()
			.name(entity.getString(name))
			.value(entity.getString(value))
			.build() : null;

	ValueFromModelMapper<OpenShiftAnnotation, Entity> fromOpenShiftAnnotation = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.stringField(value, model.getValue())
			.build() : null;
}
