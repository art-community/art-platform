package ru.art.platform.api.mapping.module;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.application.ModuleApplicationMapper;
import ru.art.platform.api.mapping.assembly.AssembledArtifactMapper;
import ru.art.platform.api.mapping.configuration.PreparedConfigurationIdentifierMapper;
import ru.art.platform.api.mapping.external.ExternalIdentifierMapper;
import ru.art.platform.api.mapping.external.PortMappingMapper;
import ru.art.platform.api.mapping.file.PlatformFileIdentifierMapper;
import ru.art.platform.api.mapping.file.StringFileMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.module.Module;

public interface ModuleMapper {
	String id = "id";

	String name = "name";

	String projectId = "projectId";

	String externalId = "externalId";

	String internalIp = "internalIp";

	String resourceId = "resourceId";

	String artifact = "artifact";

	String url = "url";

	String parameters = "parameters";

	String count = "count";

	String state = "state";

	String updateTimeStamp = "updateTimeStamp";

	String ports = "ports";

	String portMappings = "portMappings";

	String preparedConfigurations = "preparedConfigurations";

	String manualConfigurations = "manualConfigurations";

	String additionalFiles = "additionalFiles";

	String applications = "applications";

	ValueToModelMapper<Module, Entity> toModule = entity -> isNotEmpty(entity) ? Module.builder()
			.id(entity.getLong(id))
			.name(entity.getString(name))
			.projectId(entity.getLong(projectId))
			.externalId(entity.getValue(externalId, ExternalIdentifierMapper.toExternalIdentifier))
			.internalIp(entity.getString(internalIp))
			.resourceId(entity.getValue(resourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.artifact(entity.getValue(artifact, AssembledArtifactMapper.toAssembledArtifact))
			.url(entity.getValue(url, ModuleUrlMapper.toModuleUrl))
			.parameters(entity.getString(parameters))
			.count(entity.getInt(count))
			.state(entity.getString(state))
			.updateTimeStamp(entity.getLong(updateTimeStamp))
			.ports(entity.getIntList(ports))
			.portMappings(entity.getEntityList(portMappings, PortMappingMapper.toPortMapping))
			.preparedConfigurations(entity.getEntitySet(preparedConfigurations, PreparedConfigurationIdentifierMapper.toPreparedConfigurationIdentifier))
			.manualConfigurations(entity.getEntitySet(manualConfigurations, StringFileMapper.toStringFile))
			.additionalFiles(entity.getEntitySet(additionalFiles, PlatformFileIdentifierMapper.toPlatformFileIdentifier))
			.applications(entity.getEntityList(applications, ModuleApplicationMapper.toModuleApplication))
			.build() : null;

	ValueFromModelMapper<Module, Entity> fromModule = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.stringField(name, model.getName())
			.longField(projectId, model.getProjectId())
			.entityField(externalId, model.getExternalId(), ExternalIdentifierMapper.fromExternalIdentifier)
			.stringField(internalIp, model.getInternalIp())
			.entityField(resourceId, model.getResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.entityField(artifact, model.getArtifact(), AssembledArtifactMapper.fromAssembledArtifact)
			.entityField(url, model.getUrl(), ModuleUrlMapper.fromModuleUrl)
			.stringField(parameters, model.getParameters())
			.intField(count, model.getCount())
			.stringField(state, model.getState())
			.longField(updateTimeStamp, model.getUpdateTimeStamp())
			.intCollectionField(ports, model.getPorts())
			.entityCollectionField(portMappings, model.getPortMappings(), PortMappingMapper.fromPortMapping)
			.entityCollectionField(preparedConfigurations, model.getPreparedConfigurations(), PreparedConfigurationIdentifierMapper.fromPreparedConfigurationIdentifier)
			.entityCollectionField(manualConfigurations, model.getManualConfigurations(), StringFileMapper.fromStringFile)
			.entityCollectionField(additionalFiles, model.getAdditionalFiles(), PlatformFileIdentifierMapper.fromPlatformFileIdentifier)
			.entityCollectionField(applications, model.getApplications(), ModuleApplicationMapper.fromModuleApplication)
			.build() : null;
}
