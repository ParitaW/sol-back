package project.sol.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    public Note getNoteById(String id){
        return noteRepository.findById(id).orElse(null);
    }

    public List<Note> getNoteByMonth(int year, int month){
        // LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        // LocalDateTime end = start.plusMonths(1);
        // format date prefix
        String datePrefix = String.format("%04d-%02d", year, month); // yyyy-MM

        //        return notes.stream().filter(note-> note.getImageId()!=null && !note.getImageId().isEmpty()).collect(Collectors.toList());
        return noteRepository.findByDateStartingWith(datePrefix);
    }

    public Note addNote(String content, String date, String time, List<String> tags, MultipartFile image) throws IOException {
        // upload image to gridFS
        ObjectId imageId = null;
        if (image != null && !image.isEmpty()) {
            Document metadata = new Document();
            // GridFSUploadOptions options = new GridFSUploadOptions().metadata(metadata);
            imageId = gridFsTemplate.store(image.getInputStream(), image.getOriginalFilename(), image.getContentType(),
                    metadata);
        }

        // save note to the database
        Note note = new Note(content, date, time, tags, imageId != null ? imageId.toHexString() : null);
        return noteRepository.save(note);
    }

    public Note editNote(String id, String content, String date, String time, List<String> tags, MultipartFile image) throws IOException {
        Optional<Note> optionalNote=noteRepository.findById(id);
        if(optionalNote.isEmpty()){
            throw new IllegalArgumentException("Note not found");
        }

        Note existingNote=optionalNote.get();

        // ถ้ามีรูปใหม่ ส่งเข้ามา -> ลบรูปเดิม (ถ้ามี) แล้วอัปโหลดใหม่
        if (image != null && !image.isEmpty()) {
            // ลบรูปเก่าออกจาก GridFS (ถ้ามี)
            if (existingNote.getImageId() != null) {
                gridFsTemplate.delete(Query.query(Criteria.where("_id").is(existingNote.getImageId())));
            }

            // up new image
            Document metadata = new Document();
            ObjectId newImageId = gridFsTemplate.store(image.getInputStream(), image.getOriginalFilename(), image.getContentType(), metadata);
            existingNote.setImageId(newImageId.toHexString());
        }

        // update note date
        existingNote.setContent(content);
        existingNote.setDate(date);
        existingNote.setTime(time);
        existingNote.setTags(tags);
        return noteRepository.save(existingNote);
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

    public byte[] getImageById(String imageId) throws IOException {
        GridFSFile file = gridFsTemplate.findOne(new Query().addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("_id").is(imageId)));
        if (file != null) {
            GridFsResource resource = gridFsTemplate.getResource(file);
            return resource.getInputStream().readAllBytes();
        }
        return null;
    }
}
