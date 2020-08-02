package ru.art.platform.api.mapping.network;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.openShift.OpenShiftPodConfigurationMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.network.NetworkAccessRequest;

public interface NetworkAccessRequestMapper {
	String resourceId = "resourceId";

	String hostName = "hostName";

	String port = "port";

	String timeout = "timeout";

	String openShiftPodConfiguration = "openShiftPodConfiguration";

	ValueToModelMapper<NetworkAccessRequest, Entity> toNetworkAccessRequest = entity -> isNotEmpty(entity) ? NetworkAccessRequest.builder()
			.resourceId(entity.getValue(resourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.hostName(entity.getString(hostName))
			.port(entity.getInt(port))
			.timeout(entity.getInt(timeout))
			.openShiftPodConfiguration(entity.getValue(openShiftPodConfiguration, OpenShiftPodConfigurationMapper.toOpenShiftPodConfiguration))
			.build() : null;

	ValueFromModelMapper<NetworkAccessRequest, Entity> fromNetworkAccessRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(resourceId, model.getResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.stringField(hostName, model.getHostName())
			.intField(port, model.getPort())
			.intField(timeout, model.getTimeout())
			.entityField(openShiftPodConfiguration, model.getOpenShiftPodConfiguration(), OpenShiftPodConfigurationMapper.fromOpenShiftPodConfiguration)
			.build() : null;
}
