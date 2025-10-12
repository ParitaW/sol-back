package project.sol.authentication;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import project.sol.authentication.DTO.LoginRequestDTO;
import project.sol.authentication.DTO.LoginResponseDTO;
import project.sol.user.UserAccounts;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for authentication module")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public UserAccounts register(@RequestBody UserAccounts user) {
        return authenticationService.register(user);
    }

    @PostMapping(value= "/login",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO user) {
        return ResponseEntity.ok(authenticationService.login(user));
    }
}