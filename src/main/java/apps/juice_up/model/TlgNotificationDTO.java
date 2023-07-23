package apps.juice_up.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TlgNotificationDTO {

    private Long id;

    @NotNull
    private OffsetDateTime executeTimestamp;

    @Size(max = 255)
    private String message;

    private Long recipientId;

    private Long todo;

}