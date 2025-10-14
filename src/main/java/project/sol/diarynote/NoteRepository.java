package project.sol.diarynote;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends MongoRepository<Notes, String> {
    List<Notes> findByUserId(String id);

    Notes findByUserIdAndId(String userId, String id);

    List<Notes> findByUserIdAndDatetimeStartingWith(String userId, String datePrefix);

    Notes findByUserIdAndImageId(String userId, String imageId);

    void deleteAllByUserId(String userId);
}
