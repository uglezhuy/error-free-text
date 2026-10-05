package com.example.error_free_text.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateTaskRequest {

    @NotBlank
    @Size(min = 3)
    @Pattern(regexp = ".*\\p{L}.*", message = "Text must contain at least one letter")
    private String text;

    @NotBlank
    @Pattern(regexp = "RU|EN", message = "Language must be RU or EN")
    private String language;

    public CreateTaskRequest() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}