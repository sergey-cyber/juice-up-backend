package apps.juice_up.domain.systemConfiguration;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter

public class TestEntityField implements Serializable {
    private int id;
    private String name;
}
