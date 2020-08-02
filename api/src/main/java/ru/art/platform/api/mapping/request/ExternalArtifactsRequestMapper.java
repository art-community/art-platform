package ru.art.platform.api.mapping.request;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.external.ExternalArtifactMapper;
import ru.art.platform.api.mapping.resource.ArtifactsResourceMapper;
import ru.art.platform.api.mapping.resource.OpenShiftResourceMapper;
import ru.art.platform.api.model.request.ExternalArtifactsRequest;

public interface ExternalArtifactsRequestMapper {
	String artifacts = "artifacts";

	String openShiftResources = "openShiftResources";

	String artifactsResources = "artifactsResources";

	ValueToModelMapper<ExternalArtifactsRequest, Entity> toExternalArtifactsRequest = entity -> isNotEmpty(entity) ? ExternalArtifactsRequest.builder()
			.artifacts(entity.getEntityList(artifacts, ExternalArtifactMapper.toExternalArtifact))
			.openShiftResources(entity.getEntityList(openShiftResources, OpenShiftResourceMapper.toOpenShiftResource))
			.artifactsResources(entity.getEntityList(artifactsResources, ArtifactsResourceMapper.toArtifactsResource))
			.build() : null;

	ValueFromModelMapper<ExternalArtifactsRequest, Entity> fromExternalArtifactsRequest = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityCollectionField(artifacts, model.getArtifacts(), ExternalArtifactMapper.fromExternalArtifact)
			.entityCollectionField(openShiftResources, model.getOpenShiftResources(), OpenShiftResourceMapper.fromOpenShiftResource)
			.entityCollectionField(artifactsResources, model.getArtifactsResources(), ArtifactsResourceMapper.fromArtifactsResource)
			.build() : null;
}
