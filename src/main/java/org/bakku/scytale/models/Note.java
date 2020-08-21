package org.bakku.scytale.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "NOTES")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identifier;
    private String content;

    @Column(name = "access_key")
    private String accessKey;

    @Column(name = "encrypted_content_salt")
    private String encryptedContentSalt;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    Note() {}

    public Note(String identifier, String content, String accessKey, String encryptedContentSalt) {
        this.identifier = identifier;
        this.content = content;
        this.accessKey = accessKey;
        this.encryptedContentSalt = encryptedContentSalt;
    }

    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getContent() {
        return content;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getEncryptedContentSalt() {
        return encryptedContentSalt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
