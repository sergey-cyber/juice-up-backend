package apps.juice_up.service;

import apps.juice_up.domain.User;
import apps.juice_up.model.JwtUserDetails;
import apps.juice_up.repos.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    public static final String USER = "USER";
    public static final String ROLE_USER = "ROLE_" + USER;

    public JwtUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public JwtUserDetails loadUserByUsername(final String username) {
        final User client = userRepository.findByName(username);
        if(client == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        final List<SimpleGrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority(ROLE_USER));
        // TODO: need user.getHash() вместо getPassword(): see https://bootify.io/spring-security/rest-api-spring-security-with-jwt.html
        return new JwtUserDetails(client.getId(), username, client.getPassword(), roles);
    }

}
