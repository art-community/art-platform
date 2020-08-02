package ru.art.platform.api.mapping.file;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.file.StringFile;

public interface StringFileMapper {
	String name = "name";

	String content = "content";

	ValueToModelMapper<StringFile, Entity> toStringFile = entity -> isNotEmpty(entity) ? StringFile.builder()
			.name(entity.getString(name))
			.content(entity.getString(content))
			.build() : null;

	ValueFromModelMapper<StringFile, Entity> fromStringFile = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(name, model.getName())
			.stringField(content, model.getContent())
			.build() : null;
}
