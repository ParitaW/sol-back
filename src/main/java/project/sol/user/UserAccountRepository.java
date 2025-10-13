package project.sol.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends MongoRepository<UserAccounts, String>{
//    UserAccounts findByUsername(String username);
    UserAccounts findByEmail(String email);
    // UserAccounts findByUid(String uid);

    Boolean existsByEmail(String email);
}
