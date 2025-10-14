package project.sol.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.sol.authentication.DTO.LoginRequestDTO;
import project.sol.authentication.DTO.LoginResponseDTO;
import project.sol.authentication.DTO.RegisterRequestDTO;
import project.sol.user.UserAccounts;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for authentication module")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO user) {

        try {
            UserAccounts newUser = authenticationService.register(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);

        } catch (RuntimeException e) {
            if (e.getMessage().equals("Email already registered")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
        
    }

    @PostMapping(value= "/login",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO user) {
        return ResponseEntity.ok(authenticationService.login(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok("Logout successful - please remove token from client storage");
    }

    // test authenticated
    @GetMapping("/test")
    public ResponseEntity<String> testAuth(Authentication authentication) {
        if (authentication != null) {
            return ResponseEntity.ok("Authenticated as: " + authentication.getName());
        }
        return ResponseEntity.ok("Not authenticated");
    }
    
}