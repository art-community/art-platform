package ru.art.platform.api.model.request;

import lombok.*;
import ru.art.platform.api.model.file.*;
import ru.art.service.validation.*;
import static ru.art.service.validation.ValidationExpressions.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class CloneFileRequest implements Validatable  {
    private final PlatformFileIdentifier currentFileId;
    private final PlatformFileIdentifier newFileId;

    @Override
    public void onValidating(Validator validator) {
        validator.validate("currentFileName", currentFileId, notNull())
                .validate("newFileName", newFileId, notNull());
    }
}
