package ru.art.platform.api.mapping.external;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.external.PortMapping;

public interface PortMappingMapper {
	String internalPort = "internalPort";

	String externalPort = "externalPort";

	ValueToModelMapper<PortMapping, Entity> toPortMapping = entity -> isNotEmpty(entity) ? PortMapping.builder()
			.internalPort(entity.getInt(internalPort))
			.externalPort(entity.getInt(externalPort))
			.build() : null;

	ValueFromModelMapper<PortMapping, Entity> fromPortMapping = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.intField(internalPort, model.getInternalPort())
			.intField(externalPort, model.getExternalPort())
			.build() : null;
}
