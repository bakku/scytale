package org.bakku.scytale.api.dto;

import javax.validation.constraints.NotNull;

public class AuthNoteRequest {
    @NotNull(message = "accessKey must be present")
    private String accessKey;

    public AuthNoteRequest() {
    }

    public AuthNoteRequest(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessKey() {
        return accessKey;
    }
}
