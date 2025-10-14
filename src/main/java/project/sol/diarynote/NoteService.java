package project.sol.diarynote;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.mongodb.client.gridfs.model.GridFSFile;

import project.sol.user.UserAccounts;
import project.sol.user.UserPrincipal;

@Service
public class NoteService {
    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    private UserAccounts getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated user");
        }
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return principal.getUser();
    }

    public List<Notes> getNotes() {
        return noteRepository.findByUserId(getCurrentUser().getId());
    }

    public List<Notes> getNoteByDate(String date) {
        List<Notes> notes = noteRepository.findByUserIdAndDatetimeStartingWith(getCurrentUser().getId(), date);

        if (notes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found or access denied");
        }
        return notes;
    }

    public Notes getNoteById(String id) {
        Notes note = noteRepository.findByUserIdAndId(getCurrentUser().getId(), id);
        if (note == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found or access denied");
        }
        return note;
    }

    public List<Notes> getNoteByMonth(int year, int month) {
        // format date prefix
        String datePrefix = String.format("%04d-%02d", year, month); // yyyy-MM
        List<Notes> notes = noteRepository.findByUserIdAndDatetimeStartingWith(getCurrentUser().getId(), datePrefix);
        if (notes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found or access denied");
        }
        return notes;
    }

    public Notes addNote(String datetime, List<String> tags, String noteContent, MultipartFile image)
            throws IOException {
        // upload image to gridFS
        ObjectId imageId = null;
        if (image != null && !image.isEmpty()) {
            Document metadata = new Document();
            imageId = gridFsTemplate.store(image.getInputStream(), image.getOriginalFilename(), image.getContentType(),
                    metadata);
        }

        String now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        Notes addon = new Notes();
        addon.setUserId(getCurrentUser().getId());
        addon.setDatetime(datetime);
        addon.setTags(tags);
        addon.setNoteContent(noteContent);
        addon.setImageId(imageId != null ? imageId.toHexString() : null);
        addon.setCreatedAt(now);
        addon.setUpdatedAt(now);

        return noteRepository.save(addon);
    }

    public Notes editNote(String id, String datetime, List<String> tags, String noteContent, MultipartFile image)
            throws IOException {
        Notes existedNote = noteRepository.findByUserIdAndId(getCurrentUser().getId(), id);

        if (existedNote == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }

        Notes existingNotes = existedNote;

        // ถ้ามีรูปใหม่ ส่งเข้ามา -> ลบรูปเดิม (ถ้ามี) แล้วอัปโหลดใหม่
        if (image != null && !image.isEmpty()) {
            // ลบรูปเก่าออกจาก GridFS (ถ้ามี)
            if (existingNotes.getImageId() != null) {
                ObjectId objectId = new ObjectId(existingNotes.getImageId());
                gridFsTemplate.delete(Query.query(Criteria.where("_id").is(objectId)));
            }

            // up new image
            Document metadata = new Document();
            ObjectId newImageId = gridFsTemplate.store(image.getInputStream(), image.getOriginalFilename(),
                    image.getContentType(), metadata);
            existingNotes.setImageId(newImageId.toHexString());
        }

        String now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        // update note date
        existingNotes.setDatetime(datetime);
        existingNotes.setTags(tags);
        existingNotes.setNoteContent(noteContent);
        existingNotes.setUpdatedAt(now);

        return noteRepository.save(existingNotes);
    }

    public void deleteNoteById(String id) {
        Notes note = noteRepository.findByUserIdAndId(getCurrentUser().getId(), id);
        // ลบรูปใน GridFS
        if (note != null) {
            if (note.getImageId() != null) {
                ObjectId objectId = new ObjectId(note.getImageId());
                gridFsTemplate.delete(Query.query(Criteria.where("_id").is(objectId)));
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }
        // ลบ note in database
        noteRepository.deleteById(id);
    }

    public void deleteAllNotes() {
        List<Notes> notes = noteRepository.findByUserId(getCurrentUser().getId());
        // delete images from gridFS
        for (Notes note : notes) {
            if (note.getImageId() != null) {
                ObjectId objectId = new ObjectId(note.getImageId());
                gridFsTemplate.delete(Query.query(Criteria.where("_id").is(objectId)));
            }
        }

        noteRepository.deleteAllByUserId(getCurrentUser().getId());
    }

    // upload image to the database
    // public String uploadImage(MultipartFile file) throws IOException {

    // return fileId.toString();
    // }

    // get image from the database
    public GridFsResource getImage(String imageId) {
        Notes note = noteRepository.findByUserIdAndImageId(getCurrentUser().getId(), imageId);

        if (note == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found or access denied");
        }

        try {
            ObjectId objectId = new ObjectId(imageId);
            GridFSFile file = gridFsTemplate.findOne(new Query().addCriteria(Criteria.where("_id").is(objectId)));
            if (file != null) {
                return gridFsTemplate.getResource(file);
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image ID format");
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
    }

    public byte[] getImageById(String imageId) throws IOException {
        Notes note = noteRepository.findByUserIdAndImageId(getCurrentUser().getId(), imageId);

        if (note == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found or access denied");
        }

        try {
            ObjectId objectId = new ObjectId(imageId);
            GridFSFile file = gridFsTemplate.findOne(new Query().addCriteria(Criteria.where("_id").is(objectId)));
            if (file != null) {
                GridFsResource resource = gridFsTemplate.getResource(file);
                return resource.getInputStream().readAllBytes();
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image ID format");
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
    }
}
