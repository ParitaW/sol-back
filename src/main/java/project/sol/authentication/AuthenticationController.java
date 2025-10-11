package project.sol.authentication;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import project.sol.user.UserService;
import project.sol.user.Users;


@RestController
@RequestMapping("/api/auth")
@Tag(name="Authentication", description = "APIs for authentication module")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
    
    @PostMapping("/register")
    public Users register(@RequestBody Users user) {
        return authenticationService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody Users user) {
        return authenticationService.login(user);
    }
}
