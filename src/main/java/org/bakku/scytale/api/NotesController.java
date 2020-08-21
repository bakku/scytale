package org.bakku.scytale.api;

import org.bakku.scytale.api.dto.CreateNoteRequest;
import org.bakku.scytale.exceptions.NoteIdentifierNotUniqueException;
import org.bakku.scytale.models.Note;
import org.bakku.scytale.persistency.NoteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/notes", produces = "application/json", consumes = "application/json")
public class NotesController {
    private final NoteRepository noteRepository;

    public NotesController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createNote(@Valid @RequestBody CreateNoteRequest createNoteRequest) throws NoteIdentifierNotUniqueException {
        var salt = KeyGenerators.string().generateKey();
        var encryptedContent = Encryptors.text(createNoteRequest.getAccessKey(), salt).encrypt(createNoteRequest.getContent());
        var hashedAccessKey = (new BCryptPasswordEncoder()).encode(createNoteRequest.getAccessKey());

        var note = new Note(createNoteRequest.getIdentifier(), encryptedContent, hashedAccessKey, salt);

        try {
            noteRepository.save(note);
        } catch (DataIntegrityViolationException ex) {
            throw new NoteIdentifierNotUniqueException();
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = NoteIdentifierNotUniqueException.class)
    protected Map<String, List<String>> handleNoteIdentifierNotUnique() {
        return Map.of(
            "errors",
            List.of("identifier is already taken")
        );
    }
}
