package project.sol.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import project.sol.model.Note;
import project.sol.service.NoteService;



@RestController
@RequestMapping("/api/notes")
public class NoteController {
    @Autowired
    private NoteService noteService;

    @GetMapping
    public List<Note> getNotes() {
        return noteService.getNotes();
    }

    @PostMapping("/add")
    public Note addNote(@RequestBody Note note) {
        return noteService.addNote(note);
    }

    @GetMapping("/{date}")
    public Optional<Note> getNoteByDate (@PathVariable String dateTime){
        return noteService.getNoteByDate(dateTime);
    }
    
    @DeleteMapping("/{id}")
    public void deleteNoteById(@PathVariable String id){
        noteService.deleteNoteById(id);
    }
    
}
