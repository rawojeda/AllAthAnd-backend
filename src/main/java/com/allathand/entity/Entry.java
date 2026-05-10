package com.allathand.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "entries")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "code_snippet", columnDefinition = "TEXT")
    private String codeSnippet;

    private String language;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "entry_tags", joinColumns = @JoinColumn(name = "entry_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "log_file")
    private String logFile;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
