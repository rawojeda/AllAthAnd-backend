package com.allathand.service;

import com.allathand.dto.CreateEntryDTO;
import com.allathand.dto.UpdateEntryDTO;
import com.allathand.entity.Entry;
import com.allathand.entity.Tag;
import com.allathand.repository.EntryRepository;
import com.allathand.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntryService {

    private final EntryRepository entryRepository;
    private final TagRepository tagRepository;

    /**
     * 
     * Avoid N+1 problem: fetch entries with tags in a single query.
     */
    public Page<Entry> getAll(String search, List<String> tags, Pageable pageable) {
        boolean hasSearch = search != null && !search.isBlank();
        boolean hasTags = tags != null && !tags.isEmpty();

        if (hasSearch && hasTags) {
            return entryRepository.searchEntriesByTags(search, tags, pageable);
        }
        if (hasSearch) {
            return entryRepository.searchEntries(search, pageable);
        }
        if (hasTags) {
            return entryRepository.findByTags(tags, pageable);
        }
        return entryRepository.findAllWithTags(pageable);
    }

    /**
     * Búsqueda por texto en título, descripción y código.
     * 
     * ¿Por qué cambio?
     * - La BD filtra antes de traer datos
     * - Se aprovecha índices de PostgreSQL
     * - Retorna solo lo necesario
     */
    public Page<Entry> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return getAll(null, null, pageable);
        }
        return entryRepository.searchEntries(query, pageable);
    }

    /**
     * Búsqueda por lenguaje de programación.
     */
    public Page<Entry> findByLanguage(String language, Pageable pageable) {
        return entryRepository.findByLanguageOrderByUpdatedAtDesc(language, pageable);
    }

    /**
     * Obtener entry por ID con tags cargados.
     * 
     * Usa findByIdWithTags() que hace JOIN FETCH para evitar:
     * - LazyInitializationException si se accede a tags fuera de transacción
     * - N+1 query (1 query por tag)
     */
    public Entry getById(String id) {
        return entryRepository.findByIdWithTags(id)
                .orElseThrow(() -> new EntityNotFoundException("Entry not found: " + id));
    }

    /**
     * Crear una nueva entry con tags.
     * 
     * Lógica:
     * 1. Si el tag existe, lo reutiliza (evita duplicados)
     * 2. Si no existe, lo crea
     * 3. Asocia todos los tags a la entry
     * 4. Guarda entry + tags (cascade=PERSIST lo maneja)
     */
    @Transactional
    public Entry create(CreateEntryDTO dto) {
        Entry entry = new Entry();
        entry.setTitle(dto.getTitle());
        entry.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
        entry.setCodeSnippet(dto.getCodeSnippet() != null ? dto.getCodeSnippet() : "");
        entry.setLanguage(dto.getLanguage() != null ? dto.getLanguage() : "plaintext");
        entry.setImageUrl(dto.getImageUrl());
        entry.setLogFile(dto.getLogFile());

        // Convertir nombres de tags (strings) a entidades Tag
        Set<Tag> tags = new HashSet<>();
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            for (String tagName : dto.getTags()) {
                // Buscar si el tag ya existe (case-insensitive)
                Tag tag = tagRepository.findByNameIgnoreCase(tagName)
                        .orElseGet(() -> new Tag(tagName));
                tags.add(tag);
            }
        }
        entry.setTags(tags);

        return entryRepository.save(entry);
    }

    /**
     * Actualizar entry existente.
     * 
     * Manejo especial de tags:
     * - Solo actualiza si dto.getTags() no es null
     * - Busca o crea cada tag
     * - Reemplaza completamente el set de tags
     */
    @Transactional
    public Entry update(String id, UpdateEntryDTO dto) {
        Entry entry = getById(id);

        if (dto.getTitle() != null) entry.setTitle(dto.getTitle());
        if (dto.getDescription() != null) entry.setDescription(dto.getDescription());
        if (dto.getCodeSnippet() != null) entry.setCodeSnippet(dto.getCodeSnippet());
        if (dto.getLanguage() != null) entry.setLanguage(dto.getLanguage());
        if (dto.getImageUrl() != null) entry.setImageUrl(dto.getImageUrl());
        if (dto.getLogFile() != null) entry.setLogFile(dto.getLogFile());

        // Actualizar tags solo si se proporcionan
        if (dto.getTags() != null) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : dto.getTags()) {
                Tag tag = tagRepository.findByNameIgnoreCase(tagName)
                        .orElseGet(() -> new Tag(tagName));
                tags.add(tag);
            }
            entry.setTags(tags);
        }

        return entryRepository.save(entry);
    }

    /**
     * Eliminar una entry.
     */
    @Transactional
    public void delete(String id) {
        if (!entryRepository.existsById(id)) {
            throw new EntityNotFoundException("Entry not found: " + id);
        }
        entryRepository.deleteById(id);
    }

    /**
     * Obtener todos los tags únicos, ordenados.
     * 
     * Cambio:
     * ❌ ANTES: Traía todas las entries, extraía tags con .stream()
     * ✅ AHORA: Solo query a tabla tags (mucho más rápido)
     */
    public List<String> getAllTags() {
        return tagRepository.findAllByOrderByNameAsc().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

    /**
     * Tags más usados (trending).
     * 
     * Útil para:
     * - Mostrar "tags populares" en el frontend
     * - Análisis: ¿qué aprenden más mis usuarios?
     */
    public List<String> getMostUsedTags(int limit) {
        return tagRepository.findMostUsedTags(limit);
    }
}

