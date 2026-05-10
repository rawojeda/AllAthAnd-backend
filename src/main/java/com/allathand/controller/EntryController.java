package com.allathand.controller;

import com.allathand.dto.CreateEntryDTO;
import com.allathand.dto.UpdateEntryDTO;
import com.allathand.entity.Entry;
import com.allathand.service.EntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entries")
@RequiredArgsConstructor
public class EntryController {

    private final EntryService entryService;

    @GetMapping
    public List<Entry> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags
    ) {
        return entryService.getAll(search, tags);
    }

    @GetMapping("/tags")
    public List<String> getAllTags() {
        return entryService.getAllTags();
    }

    @GetMapping("/{id}")
    public Entry getById(@PathVariable String id) {
        return entryService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Entry create(@Valid @RequestBody CreateEntryDTO dto) {
        return entryService.create(dto);
    }

    @PutMapping("/{id}")
    public Entry update(@PathVariable String id, @RequestBody UpdateEntryDTO dto) {
        return entryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        entryService.delete(id);
    }
}
