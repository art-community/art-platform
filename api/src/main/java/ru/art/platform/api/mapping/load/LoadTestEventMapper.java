package ru.art.platform.api.mapping.load;

import ru.art.entity.*;
import ru.art.entity.mapper.*;
import ru.art.platform.api.model.load.*;
import static ru.art.core.checker.CheckerForEmptiness.*;
import static ru.art.core.constants.ArrayConstants.*;
import static ru.art.core.extension.NullCheckingExtensions.*;
import static ru.art.platform.api.mapping.load.LoadTestMapper.fromLoadTest;
import static ru.art.platform.api.mapping.load.LoadTestMapper.toLoadTest;

public interface LoadTestEventMapper {
	String loadTest = "loadTest";

	String logRecord = "logRecord";

	String reportArchiveBytes = "reportArchiveBytes";

	ValueToModelMapper<LoadTestEvent, Entity> toLoadTestEvent = entity -> isNotEmpty(entity) ? LoadTestEvent.builder()
			.loadTest(entity.getValue(loadTest, toLoadTest))
			.logRecord(entity.getString(logRecord))
			.reportArchiveBytes(doIfNotNull(entity.getCollectionValue(reportArchiveBytes), CollectionValue::getByteArray, () -> EMPTY_BYTES))
			.build() : null;

	ValueFromModelMapper<LoadTestEvent, Entity> fromLoadTestEvent = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(loadTest, model.getLoadTest(), fromLoadTest)
			.stringField(logRecord, model.getLogRecord())
			.byteArrayField(reportArchiveBytes, getOrElse(model.getReportArchiveBytes(), EMPTY_BYTES))
			.build() : null;
}
