package com.allathand.repository;

import com.allathand.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {

    /**
     * Search exact tag by name (case-insensitive).
     * Avoids creating duplicate tags(for example, uppercase vs lowercase).
     */
    Optional<Tag> findByNameIgnoreCase(String name);

    /**
     * Orders tags alphabetically by name.
     */
    List<Tag> findAllByOrderByNameAsc();

    /**
     * Search tags by partial name match (autocomplete).
     */
    List<Tag> findByNameContainingIgnoreCaseOrderByNameAsc(String pattern);

    /**
     * Most used tags (top N).
     */
    @Query(value = """
            SELECT t.name
            FROM tags t
            LEFT JOIN entry_tags et ON t.id = et.tag_id
            GROUP BY t.id, t.name
            ORDER BY COUNT(et.entry_id) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<String> findMostUsedTags(@Param("limit") int limit);
}
