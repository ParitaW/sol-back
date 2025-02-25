package project.sol.controller;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import project.sol.model.Note;
import project.sol.service.NoteService;

@CrossOrigin
@RestController
@RequestMapping("/api/notes")
public class NoteController {
    @Autowired
    private NoteService noteService;

    // for testing notes
    @GetMapping
    public List<Note> getNotes() {
        return noteService.getNotes();
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Note> addNote(@RequestParam("content") String content, @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        if (image != null && image.getSize() > 10 * 1024 * 1024) { // 10MB
            return ResponseEntity.badRequest().build();

        }
        // S parsedDateTime = LocalDateTime.parse(datetime);
        Note addNote = noteService.addNote(content, date, time, tags, image);
        return ResponseEntity.ok(addNote);
    }

    @GetMapping("/{date}")
    public ResponseEntity<?> getNoteByDate(@PathVariable String date) {
        try {
            // validate date format
            // LocalDateTime.parse(date);

            List<Note> notes = noteService.getNoteByDate(date);
            if (notes.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(notes);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format: Please use yyyy-MM-dd");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error retrieving note by date: " + e.getMessage());
        }
    }

    @GetMapping("/calendar/{year}/{month}")
    public ResponseEntity<?> getCalendarImageView(@PathVariable int year, @PathVariable int month){
        try{
            List<Note> notes = noteService.getImageByMonth(year, month);
            if(notes.isEmpty()){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error retrieving image by month: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteNoteById(@PathVariable String id) {
        noteService.deleteNoteById(id);
    }

    // for testing
    @DeleteMapping("/delete")
    public void deleteAllNotes() {
        noteService.deleteAllNotes();
    }

}
