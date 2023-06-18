package apps.juice_up.model;

import apps.juice_up.domain.Todo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
public class ScopeDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String name;

    private Set<Todo> todos;

    private Long user;

}
