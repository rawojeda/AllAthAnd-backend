package com.allathand.controller;

import com.allathand.dto.CreateEntryDTO;
import com.allathand.dto.EntryResponseDTO;
import com.allathand.dto.UpdateEntryDTO;
import com.allathand.service.EntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entries")
@RequiredArgsConstructor
public class EntryController {

    private final EntryService entryService;

    @GetMapping
    public Page<EntryResponseDTO> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags,
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return entryService.getAll(search, tags, pageable);
    }

    @GetMapping("/search")
    public Page<EntryResponseDTO> search(
            @RequestParam String q,
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return entryService.search(q, pageable);
    }

    @GetMapping("/language/{language}")
    public Page<EntryResponseDTO> findByLanguage(
            @PathVariable String language,
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return entryService.findByLanguage(language, pageable);
    }

    @GetMapping("/tags")
    public List<String> getAllTags() {
        return entryService.getAllTags();
    }

    @GetMapping("/tags/trending")
    public List<String> getTrendingTags(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return entryService.getMostUsedTags(limit);
    }

    @GetMapping("/{id}")
    public EntryResponseDTO getById(@PathVariable String id) {
        return entryService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntryResponseDTO create(@Valid @RequestBody CreateEntryDTO dto) {
        return entryService.create(dto);
    }

    @PutMapping("/{id}")
    public EntryResponseDTO update(@PathVariable String id, @RequestBody UpdateEntryDTO dto) {
        return entryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        entryService.delete(id);
    }
}
