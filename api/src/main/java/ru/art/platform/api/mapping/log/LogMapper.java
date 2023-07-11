package ru.art.platform.api.mapping.log;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.log.Log;

public interface LogMapper {
	String id = "id";

	String records = "records";

	ValueToModelMapper<Log, Entity> toLog = entity -> isNotEmpty(entity) ? Log.builder()
			.id(entity.getLong(id))
			.records(entity.getStringList(records))
			.build() : null;

	ValueFromModelMapper<Log, Entity> fromLog = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.stringCollectionField(records, model.getRecords())
			.build() : null;
}
