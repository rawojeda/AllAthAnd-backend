package com.allathand.repository;

import com.allathand.entity.Entry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntryRepository extends JpaRepository<Entry, String> {

    /**
     * Search entries by title (case-insensitive, partial match).
     * ordered by updatedAt desc and paginated.
     */
    Page<Entry> findByTitleContainingIgnoreCaseOrderByUpdatedAtDesc(String search, Pageable pageable);

    /**
     * Search entries by programming language.
     * ordered by updatedAt desc and paginated.
     */
    Page<Entry> findByLanguageOrderByUpdatedAtDesc(String language, Pageable pageable);

    /**
     * Advanced search: search by title, description, tags or code snippet (case-insensitive, partial match).
     * ordered by updatedAt desc and paginated.
     */
    @Query("""
            SELECT DISTINCT e FROM Entry e
            LEFT JOIN FETCH e.tags t
            WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(e.codeSnippet) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))
            ORDER BY e.updatedAt DESC
            """)
    Page<Entry> searchEntries(@Param("search") String search, Pageable pageable);

    @Query("""
            SELECT DISTINCT e FROM Entry e
            LEFT JOIN FETCH e.tags t
            WHERE t.name IN :tags
            ORDER BY e.updatedAt DESC
            """)
    Page<Entry> findByTags(@Param("tags") List<String> tags, Pageable pageable);

    @Query("""
            SELECT DISTINCT e FROM Entry e
            LEFT JOIN FETCH e.tags t
            WHERE (LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(e.codeSnippet) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')))
              AND t.name IN :tags
            ORDER BY e.updatedAt DESC
            """)
    Page<Entry> searchEntriesByTags(@Param("search") String search, @Param("tags") List<String> tags, Pageable pageable);

    /**
     * Find entry by ID with tags loaded (JOIN FETCH).
     * Avoids LazyInitializationException when accessing tags outside of a transaction.
     */
    @Query("""
            SELECT e FROM Entry e
            LEFT JOIN FETCH e.tags
            WHERE e.id = :id
            """)
    Optional<Entry> findByIdWithTags(@Param("id") String id);

    /**
     * All entries, but with tags preloaded (JOIN FETCH - main screen).
     */
    @Query("""
            SELECT DISTINCT e FROM Entry e
            LEFT JOIN FETCH e.tags
            ORDER BY e.updatedAt DESC
            """)
    Page<Entry> findAllWithTags(Pageable pageable);
}

