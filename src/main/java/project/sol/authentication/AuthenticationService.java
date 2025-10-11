package project.sol.authentication;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import project.sol.jwt.JwtService;
import project.sol.user.UserRepository;
import project.sol.user.Users;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Users register(Users user) {
        // set uid with format sol-xxxxx and encode password
        user.setUid("sol-"
                + RandomStringUtils.random(5, 0, 0, true, true, null, new java.security.SecureRandom()).toLowerCase());
        user.setPassword(encoder.encode(user.getPassword()));
        String now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        // user.setLastLoginAt(now);
        return userRepository.save(user);
    }

    public String login(Users user) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if (!authentication.isAuthenticated()) {
            return null;
        }

        // fetch user from database and update last login time
        Users existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser == null) {
            return null;
        }

        existingUser.setLastLoginAt(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        userRepository.save(existingUser);

        return jwtService.generateToken(existingUser.getUsername());
    }
}
