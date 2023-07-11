package ru.art.platform.api.mapping.variable;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.variable.EnvironmentVariable;

public interface EnvironmentVariableMapper {
	String name = "name";

	String value = "value";

	ValueToModelMapper<EnvironmentVariable, Entity> toEnvironmentVariable = entity -> isNotEmpty(entity) ? EnvironmentVariable.builder()
			.name(entity.getString(name))
			.value(entity.getString(value))
			.build() : null;

	ValueFromModelMapper<EnvironmentVariable, Entity> fromEnvironmentVariable = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.stringField(value, model.getValue())
			.build() : null;
}
