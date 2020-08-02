package ru.art.platform.api.mapping.assembly;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.assembly.AssemblyEvent;

public interface AssemblyEventMapper {
	String assembly = "assembly";

	String logRecord = "logRecord";

	ValueToModelMapper<AssemblyEvent, Entity> toAssemblyEvent = entity -> isNotEmpty(entity) ? AssemblyEvent.builder()
			.assembly(entity.getValue(assembly, AssemblyMapper.toAssembly))
			.logRecord(entity.getString(logRecord))
			.build() : null;

	ValueFromModelMapper<AssemblyEvent, Entity> fromAssemblyEvent = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(assembly, model.getAssembly(), AssemblyMapper.fromAssembly)
			.stringField(logRecord, model.getLogRecord())
			.build() : null;
}
