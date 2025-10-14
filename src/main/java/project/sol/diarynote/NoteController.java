package project.sol.diarynote;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@RestController
@RequestMapping("/api/notes")
@Tag(name = "Daily note", description = "APIs for daily note module")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // for testing notes
    @Operation(summary = "Get all notes", description = "all notes")
    @GetMapping
    public ResponseEntity<List<Notes>> getNotes() {
        try {
            List<Notes> notes = noteService.getNotes();
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }
    }

    @Operation(summary = "Create note", description = "Add a new note to the system")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addNote(@RequestParam("noteContent") String noteContent,
            @RequestParam("datetime") String datetime,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        if (image != null && image.getSize() > 10 * 1024 * 1024) { // 10MB
            return ResponseEntity.badRequest().build();

        }

        try {
            Notes addNotes = noteService.addNote(datetime, tags, noteContent, image);
            return ResponseEntity.ok(addNotes);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error creating note: " + e.getMessage());
        }
    }

    @Operation(summary = "Edit note by id", description = "Edit exist note")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Notes> editNote(@PathVariable("id") String id,
            @RequestParam("noteContent") String noteContent,
            @RequestParam("datetime") String datetime,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        if (image != null && image.getSize() > 10 * 1024 * 1024) { // 10MB
            return ResponseEntity.badRequest().build();
        }
        Notes editNotes = noteService.editNote(id, datetime, tags, noteContent, image);
        return ResponseEntity.ok(editNotes);
    }

    @Operation(summary = "Get note by date", description = "Each date note")
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Notes>> getNoteByDate(@PathVariable String date) {
        try {
            List<Notes> notes = noteService.getNoteByDate(date);
            if (notes.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(notes);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid date format: Please use yyyy-MM-dd");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error retrieving note by date: " + e.getMessage());
        }
    }

    @Operation(summary = "Get note by ID", description = "each note")
    @GetMapping("/{id}")
    public ResponseEntity<Notes> getNoteById(@PathVariable String id) {
        Notes notes = noteService.getNoteById(id);
        return ResponseEntity.ok(notes);
    }

    @Operation(summary = "Get note by year/month", description = "")
    @GetMapping("/calendar/{year}/{month}")
    public ResponseEntity<?> getCalendarImageView(@PathVariable int year, @PathVariable int month) {
        try {
            List<Notes> notes = noteService.getNoteByMonth(year, month);
            if (notes.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error retrieving note by year and month: " + e.getMessage());
        }
    }

    @Operation(summary = "Delete note by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable String id) {
        noteService.deleteNoteById(id);
        return ResponseEntity.noContent().build();
    }

    // for testing
    @Operation(summary = "Delete all note", description = "")
    @DeleteMapping("/delete")
    public void deleteAllNotes() {
        noteService.deleteAllNotes();
    }

}
