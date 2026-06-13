package com.allathand.dto;

import com.allathand.entity.Entry;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
public class EntryResponseDTO {

    String id;
    String title;
    String description;
    String codeSnippet;
    String language;
    List<String> tags;
    String imageUrl;
    String logFile;
    Instant createdAt;
    Instant updatedAt;

    public static EntryResponseDTO from(Entry entry) {
        List<String> tagNames = entry.getTags().stream()
                .map(tag -> tag.getName())
                .sorted()
                .collect(Collectors.toList());

        return EntryResponseDTO.builder()
                .id(entry.getId())
                .title(entry.getTitle())
                .description(entry.getDescription())
                .codeSnippet(entry.getCodeSnippet())
                .language(entry.getLanguage())
                .tags(tagNames)
                .imageUrl(entry.getImageUrl())
                .logFile(entry.getLogFile())
                .createdAt(entry.getCreatedAt())
                .updatedAt(entry.getUpdatedAt())
                .build();
    }
}
