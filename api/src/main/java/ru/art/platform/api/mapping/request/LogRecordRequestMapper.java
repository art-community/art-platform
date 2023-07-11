package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.request.LogRecordRequest;

public interface LogRecordRequestMapper {
	String logId = "logId";

	String record = "record";

	ValueToModelMapper<LogRecordRequest, Entity> toLogRecordRequest = entity -> isNotEmpty(entity) ? LogRecordRequest.builder()
			.logId(entity.getLong(logId))
			.record(entity.getString(record))
			.build() : null;

	ValueFromModelMapper<LogRecordRequest, Entity> fromLogRecordRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(logId, model.getLogId())
			.stringField(record, model.getRecord())
			.build() : null;
}
