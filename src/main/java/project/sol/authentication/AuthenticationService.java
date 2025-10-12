package project.sol.authentication;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import project.sol.authentication.DTO.LoginRequestDTO;
import project.sol.authentication.DTO.LoginResponseDTO;
import project.sol.jwt.JwtService;
import project.sol.user.UserAccounts;
import project.sol.user.UserAccountRepository;

@Service
public class AuthenticationService {
    private final UserAccountRepository userAccountRepository;

    final
    AuthenticationManager authenticationManager;

    final
    JwtService jwtService;

    public AuthenticationService(UserAccountRepository userAccountRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userAccountRepository = userAccountRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserAccounts register(UserAccounts user) {
        // set uid with format sol-xxxxx and encode password
        user.setUid("sol-"
                + RandomStringUtils.random(5, 0, 0, true, true, null, new java.security.SecureRandom()).toLowerCase());
        user.setPassword(encoder.encode(user.getPassword()));
        String now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        // user.setLastLoginAt(now);
        return userAccountRepository.save(user);
    }

    public LoginResponseDTO login(LoginRequestDTO user) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        if (!authentication.isAuthenticated()) {
            throw new RuntimeException("Invalid credentials");
        }

        // fetch user from database and update last login time
        UserAccounts existingUser = userAccountRepository.findByEmail(user.getEmail());
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }

        existingUser.setLastLoginAt(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        userAccountRepository.save(existingUser);

        String token = jwtService.generateToken(existingUser.getEmail());

        return new LoginResponseDTO(token); // 10 hours
    }
}
