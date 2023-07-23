package apps.juice_up.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class JwtUserDetails extends User {

    public final Long id;

    public final Long telegramId;

    public JwtUserDetails(final Long id, final Long telegramId, final String username, final String hash,
                          final Collection<? extends GrantedAuthority> authorities) {
        super(username, hash, authorities);
        this.id = id;
        this.telegramId = telegramId;
    }

}
