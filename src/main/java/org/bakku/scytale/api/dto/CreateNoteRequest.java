package org.bakku.scytale.api.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class CreateNoteRequest {
    @NotNull(message = "identifier must be present")
    @Pattern(regexp = "^[A-Za-z_-]+", message = "identifier must only contain letters, underscores, and hyphens")
    private final String identifier;
    @NotNull(message = "content must be present")
    private final String content;
    @NotNull(message = "accessKey must be present")
    private final String accessKey;

    public CreateNoteRequest(String identifier, String content, String accessKey) {
        this.identifier = identifier;
        this.content = content;
        this.accessKey = accessKey;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getContent() {
        return content;
    }

    public String getAccessKey() {
        return accessKey;
    }
}
