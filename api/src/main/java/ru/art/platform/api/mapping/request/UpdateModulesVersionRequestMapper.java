package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.request.UpdateModulesVersionRequest;

public interface UpdateModulesVersionRequestMapper {
	String version = "version";

	String ids = "ids";

	ValueToModelMapper<UpdateModulesVersionRequest, Entity> toUpdateModulesVersionRequest = entity -> isNotEmpty(entity) ? UpdateModulesVersionRequest.builder()
			.version(entity.getString(version))
			.ids(entity.getLongList(ids))
			.build() : null;

	ValueFromModelMapper<UpdateModulesVersionRequest, Entity> fromUpdateModulesVersionRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.stringField(version, model.getVersion())
			.longCollectionField(ids, model.getIds())
			.build() : null;
}
