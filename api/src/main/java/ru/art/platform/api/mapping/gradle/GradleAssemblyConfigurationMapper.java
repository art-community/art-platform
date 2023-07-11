package ru.art.platform.api.mapping.gradle;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.mapping.property.PropertyMapper;
import ru.art.platform.api.model.gradle.GradleAssemblyConfiguration;

public interface GradleAssemblyConfigurationMapper {
	String cacheConfiguration = "cacheConfiguration";

	String arguments = "arguments";

	String jdkVersion = "jdkVersion";

	String version = "version";

	String initScriptGroovyContent = "initScriptGroovyContent";

	String initScriptKotlinContent = "initScriptKotlinContent";

	String initScriptFormat = "initScriptFormat";

	String properties = "properties";

	ValueToModelMapper<GradleAssemblyConfiguration, Entity> toGradleAssemblyConfiguration = entity -> isNotEmpty(entity) ? GradleAssemblyConfiguration.builder()
			.cacheConfiguration(entity.getValue(cacheConfiguration, GradleCacheConfigurationMapper.toGradleCacheConfiguration))
			.arguments(entity.getString(arguments))
			.jdkVersion(entity.getString(jdkVersion))
			.version(entity.getString(version))
			.initScriptGroovyContent(entity.getString(initScriptGroovyContent))
			.initScriptKotlinContent(entity.getString(initScriptKotlinContent))
			.initScriptFormat(entity.getString(initScriptFormat))
			.properties(entity.getEntityList(properties, PropertyMapper.toProperty))
			.build() : null;

	ValueFromModelMapper<GradleAssemblyConfiguration, Entity> fromGradleAssemblyConfiguration = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.entityField(cacheConfiguration, model.getCacheConfiguration(), GradleCacheConfigurationMapper.fromGradleCacheConfiguration)
			.stringField(arguments, model.getArguments())
			.stringField(jdkVersion, model.getJdkVersion())
			.stringField(version, model.getVersion())
			.stringField(initScriptGroovyContent, model.getInitScriptGroovyContent())
			.stringField(initScriptKotlinContent, model.getInitScriptKotlinContent())
			.stringField(initScriptFormat, model.getInitScriptFormat())
			.entityCollectionField(properties, model.getProperties(), PropertyMapper.fromProperty)
			.build() : null;
}
