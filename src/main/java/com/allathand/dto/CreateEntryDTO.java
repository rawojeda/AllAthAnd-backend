package com.allathand.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateEntryDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private String codeSnippet;
    private String language;
    private List<String> tags;
    private String imageUrl;
    private String logFile;
}
