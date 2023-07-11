package ru.art.platform.api.mapping.subject;

import static ru.art.core.checker.CheckerForEmptiness.isNotEmpty;

import java.lang.String;
import ru.art.entity.Entity;
import ru.art.entity.mapper.ValueFromModelMapper;
import ru.art.entity.mapper.ValueToModelMapper;
import ru.art.platform.api.model.subject.Subject;

public interface SubjectMapper {
	String id = "id";

	String kind = "kind";

	ValueToModelMapper<Subject, Entity> toSubject = entity -> isNotEmpty(entity) ? Subject.builder()
			.id(entity.getLong(id))
			.kind(entity.getString(kind))
			.build() : null;

	ValueFromModelMapper<Subject, Entity> fromSubject = model -> isNotEmpty(model) ? Entity.entityBuilder()
			.longField(id, model.getId())
			.stringField(kind, model.getKind())
			.build() : null;
}
