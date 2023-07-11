package ru.art.platform.api.mapping.property;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.property.TextProperty;

public interface TextPropertyMapper {
	String value = "value";

	ValueToModelMapper<TextProperty, Entity> toTextProperty = entity -> isNotEmpty(entity) ? TextProperty.builder()
			.value(entity.getString(value))
			.build() : null;

	ValueFromModelMapper<TextProperty, Entity> fromTextProperty = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(value, model.getValue())
			.build() : null;
}
