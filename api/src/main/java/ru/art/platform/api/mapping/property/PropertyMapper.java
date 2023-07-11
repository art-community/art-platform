package ru.art.platform.api.mapping.property;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.property.Property;

public interface PropertyMapper {
	String id = "id";

	String name = "name";

	String type = "type";

	String textProperty = "textProperty";

	String resourceProperty = "resourceProperty";

	ValueToModelMapper<Property, Entity> toProperty = entity -> isNotEmpty(entity) ? Property.builder()
			.id(entity.getLong(id))
			.name(entity.getString(name))
			.type(entity.getString(type))
			.textProperty(entity.getValue(textProperty, TextPropertyMapper.toTextProperty))
			.resourceProperty(entity.getValue(resourceProperty, ResourcePropertyMapper.toResourceProperty))
			.build() : null;

	ValueFromModelMapper<Property, Entity> fromProperty = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.stringField(name, model.getName())
			.stringField(type, model.getType())
			.entityField(textProperty, model.getTextProperty(), TextPropertyMapper.fromTextProperty)
			.entityField(resourceProperty, model.getResourceProperty(), ResourcePropertyMapper.fromResourceProperty)
			.build() : null;
}
