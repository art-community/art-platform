package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.assembly.ArtifactConfigurationMapper;
import ru.art.platform.api.mapping.assembly.AssemblyCacheConfigurationMapper;
import ru.art.platform.api.mapping.assembly.AssemblyConfigurationMapper;
import ru.art.platform.api.mapping.assembly.AssemblyMapper;
import ru.art.platform.api.mapping.external.ExternalIdentifierMapper;
import ru.art.platform.api.mapping.resource.ArtifactsResourceMapper;
import ru.art.platform.api.mapping.resource.GitResourceMapper;
import ru.art.platform.api.mapping.resource.OpenShiftResourceMapper;
import ru.art.platform.api.model.request.AgentProjectBuildRequest;

public interface AgentProjectBuildRequestMapper {
	String projectId = "projectId";

	String assembly = "assembly";

	String assemblyConfiguration = "assemblyConfiguration";

	String cacheConfiguration = "cacheConfiguration";

	String gitResource = "gitResource";

	String artifactConfigurations = "artifactConfigurations";

	String openShiftResources = "openShiftResources";

	String artifactsResources = "artifactsResources";

	ValueToModelMapper<AgentProjectBuildRequest, Entity> toAgentProjectBuildRequest = entity -> isNotEmpty(entity) ? AgentProjectBuildRequest.builder()
			.projectId(entity.getValue(projectId, ExternalIdentifierMapper.toExternalIdentifier))
			.assembly(entity.getValue(assembly, AssemblyMapper.toAssembly))
			.assemblyConfiguration(entity.getValue(assemblyConfiguration, AssemblyConfigurationMapper.toAssemblyConfiguration))
			.cacheConfiguration(entity.getValue(cacheConfiguration, AssemblyCacheConfigurationMapper.toAssemblyCacheConfiguration))
			.gitResource(entity.getValue(gitResource, GitResourceMapper.toGitResource))
			.artifactConfigurations(entity.getEntitySet(artifactConfigurations, ArtifactConfigurationMapper.toArtifactConfiguration))
			.openShiftResources(entity.getEntitySet(openShiftResources, OpenShiftResourceMapper.toOpenShiftResource))
			.artifactsResources(entity.getEntitySet(artifactsResources, ArtifactsResourceMapper.toArtifactsResource))
			.build() : null;

	ValueFromModelMapper<AgentProjectBuildRequest, Entity> fromAgentProjectBuildRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(projectId, model.getProjectId(), ExternalIdentifierMapper.fromExternalIdentifier)
			.entityField(assembly, model.getAssembly(), AssemblyMapper.fromAssembly)
			.entityField(assemblyConfiguration, model.getAssemblyConfiguration(), AssemblyConfigurationMapper.fromAssemblyConfiguration)
			.entityField(cacheConfiguration, model.getCacheConfiguration(), AssemblyCacheConfigurationMapper.fromAssemblyCacheConfiguration)
			.entityField(gitResource, model.getGitResource(), GitResourceMapper.fromGitResource)
			.entityCollectionField(artifactConfigurations, model.getArtifactConfigurations(), ArtifactConfigurationMapper.fromArtifactConfiguration)
			.entityCollectionField(openShiftResources, model.getOpenShiftResources(), OpenShiftResourceMapper.fromOpenShiftResource)
			.entityCollectionField(artifactsResources, model.getArtifactsResources(), ArtifactsResourceMapper.fromArtifactsResource)
			.build() : null;
}
