package apps.juice_up.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TodoDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String status;

    @Size(max = 255)
    private String day;

    @Size(max = 255)
    private String description;

    private Long scope;

    private Long user;

}
