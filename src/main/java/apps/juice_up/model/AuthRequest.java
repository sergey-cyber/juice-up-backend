package apps.juice_up.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {

    @NotNull
    @Size(max = 255)
    private String login;

    @NotNull
    @Size(max = 255)
    private String password;

}
