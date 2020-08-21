package org.bakku.scytale.persistency;

import org.bakku.scytale.models.Note;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Transactional
public class NoteRepositoryTest {
    @Autowired
    private NoteRepository noteRepository;

    @Test
    public void findByIdentifier_should_returnCorrectNote() {
        noteRepository.save(new Note("abc", "content", "key", "salt"));

        var optNote = noteRepository.findByIdentifier("abc");

        assertTrue(optNote.isPresent());

        var note = optNote.get();

        assertEquals(1, note.getId());
        assertEquals("abc", note.getIdentifier());
        assertEquals("content", note.getContent());
        assertEquals("key", note.getAccessKey());
        assertEquals("salt", note.getEncryptedContentSalt());
        assertTrue(note.getCreatedAt().isBefore(LocalDateTime.now()));
        assertTrue(note.getUpdatedAt().isBefore(LocalDateTime.now()));
    }
}
