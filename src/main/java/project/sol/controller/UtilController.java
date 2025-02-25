package project.sol.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import project.sol.service.NoteService;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class UtilController {
    @Autowired
    private NoteService noteService;
    
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