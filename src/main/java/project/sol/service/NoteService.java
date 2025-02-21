package project.sol.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import project.sol.model.Note;
import project.sol.repository.NoteRepository;

@Service
public class NoteService {
    @Autowired
    private NoteRepository noteRepository;

    public List<Note> getNotes(){
        return noteRepository.findAll();
    }

    public Optional<Note> getNoteByDate(String datetime){
        return noteRepository.findByDatetime(datetime);
    }

    public Note addNote(Note note){
        return noteRepository.save(note);
    }

    public Note updateNoteById(String id, Note note){
        note.setId(id);
        return noteRepository.save(note);
    }

    public void deleteNoteById(String id){
        noteRepository.deleteById(id);
    }
}
