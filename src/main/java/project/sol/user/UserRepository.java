package project.sol.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<Users, String>{
    Users findByUsername(String username);
    // Users findByEmail(String email);
    // Users findByUid(String uid);
}
