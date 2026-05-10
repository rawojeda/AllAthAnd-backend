package com.allathand.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateEntryDTO {

    private String title;
    private String description;
    private String codeSnippet;
    private String language;
    private List<String> tags;
    private String imageUrl;
    private String logFile;
}
