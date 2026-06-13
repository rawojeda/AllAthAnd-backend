package com.allathand.controller;

import com.allathand.dto.CreateEntryDTO;
import com.allathand.dto.EntryResponseDTO;
import com.allathand.dto.UpdateEntryDTO;
import com.allathand.exception.ErrorResponse;
import com.allathand.service.EntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Entries", description = "Manage each dev-tech-entry")
public class EntryController {

    private final EntryService entryService;

    @GetMapping
    @Operation(summary = "List entries", description = "Returns a paginated list of entries, optionally filtered by search text or tags")
    public Page<EntryResponseDTO> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags,
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return entryService.getAll(search, tags, pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "Full-text search", description = "Search entries by title, description, or code snippet content")
    public Page<EntryResponseDTO> search(
            @RequestParam String q,
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return entryService.search(q, pageable);
    }

    @GetMapping("/language/{language}")
    @Operation(summary = "Filter by programming language")
    public Page<EntryResponseDTO> findByLanguage(
            @PathVariable String language,
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return entryService.findByLanguage(language, pageable);
    }

    @GetMapping("/tags")
    @Operation(summary = "List all tags", description = "Returns all existing tag names sorted alphabetically")
    public List<String> getAllTags() {
        return entryService.getAllTags();
    }

    @GetMapping("/tags/trending")
    @Operation(summary = "Trending tags", description = "Returns the most-used tags, descending by usage count")
    public List<String> getTrendingTags(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return entryService.getMostUsedTags(limit);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get entry by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entry found"),
            @ApiResponse(responseCode = "404", description = "Entry not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public EntryResponseDTO getById(@PathVariable String id) {
        return entryService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create entry")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Entry created"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public EntryResponseDTO create(@Valid @RequestBody CreateEntryDTO dto) {
        return entryService.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update entry")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entry updated"),
            @ApiResponse(responseCode = "404", description = "Entry not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public EntryResponseDTO update(@PathVariable String id, @RequestBody UpdateEntryDTO dto) {
        return entryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete entry")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Entry deleted"),
            @ApiResponse(responseCode = "404", description = "Entry not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void delete(@PathVariable String id) {
        entryService.delete(id);
    }
}
