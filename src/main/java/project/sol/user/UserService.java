package project.sol.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccounts userAccounts = userAccountRepository.findByEmail(email);

        if (userAccounts == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new UserPrincipal(userAccounts);
    }
}
