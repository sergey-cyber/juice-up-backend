package apps.juice_up.model;

import apps.juice_up.domain.systemConfiguration.TestEntityField;
import apps.juice_up.util.OverdueTodosPolicyActions;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SystemConfigurationDTO {

    private Long id;

    private OverdueTodosPolicyActions overdueTodosPolicy;

    private TestEntityField testEntityField;

    @NotNull
    private Long user;

}