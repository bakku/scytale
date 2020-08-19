package org.bakku.scytale.persistency;

import org.bakku.scytale.models.Note;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface NoteRepository extends CrudRepository<Note, Long> {
    Optional<Note> findByIdentifier(String identifier);
}
