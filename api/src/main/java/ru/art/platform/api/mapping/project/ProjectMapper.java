package ru.art.platform.api.mapping.project;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.external.ExternalIdentifierMapper;
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper;
import ru.art.platform.api.model.project.Project;

public interface ProjectMapper {
	String id = "id";

	String name = "name";

	String gitResourceId = "gitResourceId";

	String initializationResourceId = "initializationResourceId";

	String openShiftConfiguration = "openShiftConfiguration";

	String notificationsConfiguration = "notificationsConfiguration";

	String externalId = "externalId";

	String state = "state";

	String creationTimeStamp = "creationTimeStamp";

	String technologies = "technologies";

	String versions = "versions";

	String artifacts = "artifacts";

	ValueToModelMapper<Project, Entity> toProject = entity -> isNotEmpty(entity) ? Project.builder()
			.id(entity.getLong(id))
			.name(entity.getString(name))
			.gitResourceId(entity.getValue(gitResourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.initializationResourceId(entity.getValue(initializationResourceId, ResourceIdentifierMapper.toResourceIdentifier))
			.openShiftConfiguration(entity.getValue(openShiftConfiguration, ProjectOpenShiftConfigurationMapper.toProjectOpenShiftConfiguration))
			.notificationsConfiguration(entity.getValue(notificationsConfiguration, ProjectNotificationsConfigurationMapper.toProjectNotificationsConfiguration))
			.externalId(entity.getValue(externalId, ExternalIdentifierMapper.toExternalIdentifier))
			.state(entity.getString(state))
			.creationTimeStamp(entity.getLong(creationTimeStamp))
			.technologies(entity.getStringList(technologies))
			.versions(entity.getEntitySet(versions, ProjectVersionMapper.toProjectVersion))
			.artifacts(entity.getEntitySet(artifacts, ProjectArtifactMapper.toProjectArtifact))
			.build() : null;

	ValueFromModelMapper<Project, Entity> fromProject = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.stringField(name, model.getName())
			.entityField(gitResourceId, model.getGitResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.entityField(initializationResourceId, model.getInitializationResourceId(), ResourceIdentifierMapper.fromResourceIdentifier)
			.entityField(openShiftConfiguration, model.getOpenShiftConfiguration(), ProjectOpenShiftConfigurationMapper.fromProjectOpenShiftConfiguration)
			.entityField(notificationsConfiguration, model.getNotificationsConfiguration(), ProjectNotificationsConfigurationMapper.fromProjectNotificationsConfiguration)
			.entityField(externalId, model.getExternalId(), ExternalIdentifierMapper.fromExternalIdentifier)
			.stringField(state, model.getState())
			.longField(creationTimeStamp, model.getCreationTimeStamp())
			.stringCollectionField(technologies, model.getTechnologies())
			.entityCollectionField(versions, model.getVersions(), ProjectVersionMapper.fromProjectVersion)
			.entityCollectionField(artifacts, model.getArtifacts(), ProjectArtifactMapper.fromProjectArtifact)
			.build() : null;
}
