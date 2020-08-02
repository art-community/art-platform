package ru.art.platform.api.mapping.module;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.application.ModuleApplicationMapper;
import ru.art.platform.api.mapping.assembly.AssembledArtifactMapper;
import ru.art.platform.api.mapping.configuration.PreparedConfigurationIdentifierMapper;
import ru.art.platform.api.mapping.file.PlatformFileIdentifierMapper;
import ru.art.platform.api.mapping.file.StringFileMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.module.ModuleConfiguration;

public interface ModuleConfigurationMapper {
	String resourceId = "resourceId";

	String artifact = "artifact";

	String name = "name";

	String url = "url";

	String parameters = "parameters";

	String count = "count";

	String ports = "ports";

	String preparedConfigurations = "preparedConfigurations";

	String manualConfigurations = "manualConfigurations";

	String additionalFiles = "additionalFiles";

	String applications = "applications";

	ValueToModelMapper<ModuleConfiguration, Entity> toModuleConfiguration = entity -> isNotEmpty(entity) ? ModuleConfiguration.builder()
			.resourceId(entity.getValue(resourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.artifact(entity.getValue(artifact, AssembledArtifactMapper.toAssembledArtifact))
			.name(entity.getString(name))
			.url(entity.getValue(url, ModuleUrlMapper.toModuleUrl))
			.parameters(entity.getString(parameters))
			.count(entity.getInt(count))
			.ports(entity.getIntList(ports))
			.preparedConfigurations(entity.getEntitySet(preparedConfigurations, PreparedConfigurationIdentifierMapper.toPreparedConfigurationIdentifier))
			.manualConfigurations(entity.getEntitySet(manualConfigurations, StringFileMapper.toStringFile))
			.additionalFiles(entity.getEntitySet(additionalFiles, PlatformFileIdentifierMapper.toPlatformFileIdentifier))
			.applications(entity.getEntityList(applications, ModuleApplicationMapper.toModuleApplication))
			.build() : null;

	ValueFromModelMapper<ModuleConfiguration, Entity> fromModuleConfiguration = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(resourceId, model.getResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.entityField(artifact, model.getArtifact(), AssembledArtifactMapper.fromAssembledArtifact)
			.stringField(name, model.getName())
			.entityField(url, model.getUrl(), ModuleUrlMapper.fromModuleUrl)
			.stringField(parameters, model.getParameters())
			.intField(count, model.getCount())
			.intCollectionField(ports, model.getPorts())
			.entityCollectionField(preparedConfigurations, model.getPreparedConfigurations(), PreparedConfigurationIdentifierMapper.fromPreparedConfigurationIdentifier)
			.entityCollectionField(manualConfigurations, model.getManualConfigurations(), StringFileMapper.fromStringFile)
			.entityCollectionField(additionalFiles, model.getAdditionalFiles(), PlatformFileIdentifierMapper.fromPlatformFileIdentifier)
			.entityCollectionField(applications, model.getApplications(), ModuleApplicationMapper.fromModuleApplication)
			.build() : null;
}
