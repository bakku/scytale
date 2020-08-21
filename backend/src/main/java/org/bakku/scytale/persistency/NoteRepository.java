package org.bakku.scytale.persistency;

import org.bakku.scytale.models.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    Optional<Note> findByIdentifier(String identifier);
}
