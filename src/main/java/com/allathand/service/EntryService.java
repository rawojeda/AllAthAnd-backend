package com.allathand.service;

import com.allathand.dto.CreateEntryDTO;
import com.allathand.dto.UpdateEntryDTO;
import com.allathand.entity.Entry;
import com.allathand.repository.EntryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EntryService {

    private final EntryRepository entryRepository;

    public List<Entry> getAll(String search, List<String> tags) {
        Stream<Entry> stream = entryRepository.findAll().stream();

        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            stream = stream.filter(e ->
                    e.getTitle().toLowerCase().contains(q) ||
                    (e.getDescription() != null && e.getDescription().toLowerCase().contains(q)) ||
                    (e.getCodeSnippet() != null && e.getCodeSnippet().toLowerCase().contains(q)) ||
                    e.getTags().stream().anyMatch(t -> t.toLowerCase().contains(q))
            );
        }

        if (tags != null && !tags.isEmpty()) {
            stream = stream.filter(e -> tags.stream().anyMatch(t -> e.getTags().contains(t)));
        }

        return stream
                .sorted(Comparator.comparing(Entry::getUpdatedAt).reversed())
                .toList();
    }

    public Entry getById(String id) {
        return entryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entry not found: " + id));
    }

    @Transactional
    public Entry create(CreateEntryDTO dto) {
        Entry entry = new Entry();
        entry.setTitle(dto.getTitle());
        entry.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
        entry.setCodeSnippet(dto.getCodeSnippet() != null ? dto.getCodeSnippet() : "");
        entry.setLanguage(dto.getLanguage() != null ? dto.getLanguage() : "plaintext");
        entry.setTags(dto.getTags() != null ? dto.getTags() : List.of());
        entry.setImageUrl(dto.getImageUrl());
        entry.setLogFile(dto.getLogFile());
        return entryRepository.save(entry);
    }

    @Transactional
    public Entry update(String id, UpdateEntryDTO dto) {
        Entry entry = getById(id);
        if (dto.getTitle() != null) entry.setTitle(dto.getTitle());
        if (dto.getDescription() != null) entry.setDescription(dto.getDescription());
        if (dto.getCodeSnippet() != null) entry.setCodeSnippet(dto.getCodeSnippet());
        if (dto.getLanguage() != null) entry.setLanguage(dto.getLanguage());
        if (dto.getTags() != null) entry.setTags(dto.getTags());
        if (dto.getImageUrl() != null) entry.setImageUrl(dto.getImageUrl());
        if (dto.getLogFile() != null) entry.setLogFile(dto.getLogFile());
        return entryRepository.save(entry);
    }

    @Transactional
    public void delete(String id) {
        if (!entryRepository.existsById(id)) {
            throw new EntityNotFoundException("Entry not found: " + id);
        }
        entryRepository.deleteById(id);
    }

    public List<String> getAllTags() {
        return entryRepository.findAll().stream()
                .flatMap(e -> e.getTags().stream())
                .distinct()
                .sorted()
                .toList();
    }
}
