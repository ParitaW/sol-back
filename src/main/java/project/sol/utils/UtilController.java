package project.sol.utils;

import java.io.IOException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import project.sol.diarynote.NoteService;

@CrossOrigin
@RestController
@RequestMapping("/api")
@Tag(name = "Utils", description = "APIs for utils")
public class UtilController {
    private final NoteService noteService;

    public UtilController(NoteService noteService) {
        this.noteService = noteService;
    }

    @Operation(summary = "Get image by id", description = "Get image")
    @GetMapping("/images/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageId) throws IOException {
        byte[] imageData = noteService.getImageById(imageId);

        if (imageData != null) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
}