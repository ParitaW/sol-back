package project.sol.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import project.sol.model.Note;


@Repository
public interface NoteRepository extends MongoRepository<Note, String>{
    Optional<Note> findById(String id);

    List<Note> findByDatetimeStringWith(String datePrefix);
    
}
