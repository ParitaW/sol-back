package project.sol.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.gridfs.model.GridFSFile;

import project.sol.model.Note;
import project.sol.repository.NoteRepository;

@Service
public class NoteService {
    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    public List<Note> getNotes() {
        return noteRepository.findAll();
    }

    public List<Note> getNoteByDate(String date) {
        return noteRepository.findByDateStartingWith(date);
    }

    public List<Note> getImageByMonth(int year, int month){
        // LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        // LocalDateTime end = start.plusMonths(1);
        // format date prefix
        String datePrefix = String.format("%04d-%02d", year, month); // yyyy-MM

        List<Note> notes = noteRepository.findByDateStartingWith(datePrefix);
        return notes.stream().filter(note-> note.getImageId()!=null && !note.getImageId().isEmpty()).collect(Collectors.toList());
    }

    public Note addNote(String text, String date, String time, List<String> tags, MultipartFile image) throws IOException {
        // upload image to gridFS
        ObjectId imageId = null;
        if (image != null && !image.isEmpty()) {
            Document metadata = new Document();
            // GridFSUploadOptions options = new GridFSUploadOptions().metadata(metadata);
            imageId = gridFsTemplate.store(image.getInputStream(), image.getOriginalFilename(), image.getContentType(),
                    metadata);
        }

        // save note to the database
        Note note = new Note(text, date, time, tags, imageId != null ? imageId.toHexString() : null);
        return noteRepository.save(note);
    }

    public Note updateNoteById(String id, Note note) {
        note.setId(id);
        return noteRepository.save(note);
    }

    public void deleteNoteById(String id) {
        noteRepository.deleteById(id);
    }

    public void deleteAllNotes() {
        // delete images from gridFS
        gridFsTemplate.delete(new org.springframework.data.mongodb.core.query.Query());
        noteRepository.deleteAll();
    }

    // upload image to the database
    // public String uploadImage(MultipartFile file) throws IOException {

    // return fileId.toString();
    // }

    // get image from the database
    public GridFsResource getImage(String imageId) {
        GridFSFile file = gridFsTemplate.findOne(new org.springframework.data.mongodb.core.query.Query()
                .addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("_id").is(imageId)));

        if (file != null) {
            return gridFsTemplate.getResource(file);
        }
        return null;
    }
}
