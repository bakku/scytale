package org.bakku.scytale.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bakku.scytale.api.dto.AuthNoteRequest;
import org.bakku.scytale.api.dto.CreateNoteRequest;
import org.bakku.scytale.models.Note;
import org.bakku.scytale.persistency.NoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@Transactional
public class NotesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NoteRepository noteRepository;

    @Test
    public void post_should_createNewNote() throws Exception {
        this.mockMvc.perform(post("/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateNoteRequest("test-note", "content", "key"))))
            .andExpect(status().isCreated())
            .andExpect(content().string(""))
            .andReturn();

        var notes = noteRepository.findAll();

        assertEquals(1, notes.size());

        var note = notes.get(0);
        assertNotNull(note.getEncryptedContentSalt());
        assertNotEquals("content", note.getContent());
        assertEquals("content", Encryptors.text("key", note.getEncryptedContentSalt()).decrypt(note.getContent()));
        assertNotEquals("key", note.getAccessKey());
        assertTrue((new BCryptPasswordEncoder().matches("key", note.getAccessKey())));
        assertEquals("test-note", note.getIdentifier());
    }

    @Test
    public void post_should_validateRequest() throws Exception {
        var result = this.mockMvc.perform(post("/api/notes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new CreateNoteRequest(null, null, null))))
            .andExpect(status().isBadRequest())
            .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"errors\":"));
        assertTrue(result.getResponse().getContentAsString().contains("\"identifier must be present\""));
        assertTrue(result.getResponse().getContentAsString().contains("\"content must be present\""));
        assertTrue(result.getResponse().getContentAsString().contains("\"accessKey must be present\""));
    }

    @Test
    public void post_should_validateIdentifier() throws Exception {
        var result = this.mockMvc.perform(post("/api/notes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new CreateNoteRequest("A$%/", "content", "key"))))
            .andExpect(status().isBadRequest())
            .andReturn();

        assertEquals(
            result.getResponse().getContentAsString(),
            "{\"errors\":[\"identifier must only contain letters, underscores, and hyphens\"]}"
        );
    }

    @Test
    public void post_should_validateUniquenessOfIdentifier() throws Exception {
        this.mockMvc.perform(post("/api/notes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new CreateNoteRequest("test-note", "content", "key"))))
            .andExpect(status().isCreated());

        var result = this.mockMvc.perform(post("/api/notes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new CreateNoteRequest("test-note", "content", "key"))))
            .andExpect(status().isBadRequest())
            .andReturn();

        assertEquals(
            result.getResponse().getContentAsString(),
            "{\"errors\":[\"identifier is already taken\"]}"
        );
    }

    @Test
    public void postAuth_should_returnCorrectContent() throws Exception {
        var plaintextKey = "pass";
        var plaintextContent = "secret";
        var salt = KeyGenerators.string().generateKey();
        var encryptedContent = Encryptors.text(plaintextKey, salt).encrypt(plaintextContent);
        var hashedKey = (new BCryptPasswordEncoder()).encode(plaintextKey);

        var note = new Note("test-note", encryptedContent, hashedKey, salt);
        noteRepository.save(note);

        this.mockMvc.perform(post("/api/notes/test-note/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new AuthNoteRequest("pass"))))
            .andExpect(status().isOk())
            .andExpect(content().string("{\"content\":\"secret\"}"));
    }

    @Test
    public void postAuth_should_notReturnContentIfKeyIsFalse() throws Exception {
        var plaintextKey = "pass";
        var plaintextContent = "secret";
        var salt = KeyGenerators.string().generateKey();
        var encryptedContent = Encryptors.text(plaintextKey, salt).encrypt(plaintextContent);
        var hashedKey = (new BCryptPasswordEncoder()).encode(plaintextKey);

        var note = new Note("test-note", encryptedContent, hashedKey, salt);
        noteRepository.save(note);

        this.mockMvc.perform(post("/api/notes/test-note/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new AuthNoteRequest("passx"))))
            .andExpect(status().isForbidden());
    }

    @Test
    public void postAuth_should_returnNotFoundIfIdentifierDoesNotExist() throws Exception {
        var plaintextKey = "pass";
        var plaintextContent = "secret";
        var salt = KeyGenerators.string().generateKey();
        var encryptedContent = Encryptors.text(plaintextKey, salt).encrypt(plaintextContent);
        var hashedKey = (new BCryptPasswordEncoder()).encode(plaintextKey);

        var note = new Note("test-note", encryptedContent, hashedKey, salt);
        noteRepository.save(note);

        this.mockMvc.perform(post("/api/notes/test-notex/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new AuthNoteRequest("pass"))))
            .andExpect(status().isNotFound());
    }

    @Test
    public void postAuth_should_validateRequest() throws Exception {
        var plaintextKey = "pass";
        var plaintextContent = "secret";
        var salt = KeyGenerators.string().generateKey();
        var encryptedContent = Encryptors.text(plaintextKey, salt).encrypt(plaintextContent);
        var hashedKey = (new BCryptPasswordEncoder()).encode(plaintextKey);

        var note = new Note("test-note", encryptedContent, hashedKey, salt);
        noteRepository.save(note);

        this.mockMvc.perform(post("/api/notes/test-notex/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new AuthNoteRequest(null))))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("{\"errors\":[\"accessKey must be present\"]}"));
    }

    @Test
    public void getExists_should_returnOkIfIdentifierIsNotTaken() throws Exception {
        this.mockMvc.perform(get("/api/notes/test-note/exists"))
            .andExpect(status().isOk());
    }

    @Test
    public void getExists_should_returnBadRequestIfIdentifierIsTaken() throws Exception {
        noteRepository.save(new Note("test-note", "bla", "bla", "bla"));

        this.mockMvc.perform(get("/api/notes/test-note/exists"))
            .andExpect(status().isBadRequest());
    }
}
