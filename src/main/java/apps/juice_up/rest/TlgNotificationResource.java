package apps.juice_up.rest;

import apps.juice_up.model.JwtUserDetails;
import apps.juice_up.model.TlgNotificationDTO;
import apps.juice_up.service.TlgNotificationService;
import apps.juice_up.service.TodoService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.POST})
@RestController
@RequestMapping(value = "/api/tlgNotifications", produces = MediaType.APPLICATION_JSON_VALUE)
public class TlgNotificationResource {

    private final TlgNotificationService tlgNotificationService;
    private final TodoService todoService;

    public TlgNotificationResource(final TlgNotificationService tlgNotificationService, TodoService todoService) {
        this.tlgNotificationService = tlgNotificationService;
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<TlgNotificationDTO>> getAllTlgNotifications() {
        return ResponseEntity.ok(tlgNotificationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TlgNotificationDTO> getTlgNotification(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(tlgNotificationService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createTlgNotification(
            @RequestBody @Valid final TlgNotificationDTO tlgNotificationDTO,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        if(principal.getTelegramId() == null) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "You are not subscribed to the notification mailing list. To fix this send a message to the bot"
            );
        }
        final Long createdId = tlgNotificationService.create(tlgNotificationDTO);
        var todoDto = todoService.get(tlgNotificationDTO.getTodo());
        todoDto.setTlgNotification(createdId);
        todoService.update(todoDto.getId(), todoDto);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateTlgNotification(@PathVariable(name = "id") final Long id,
                                                      @RequestBody @Valid final TlgNotificationDTO tlgNotificationDTO) {
        tlgNotificationService.update(id, tlgNotificationDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteTlgNotification(@PathVariable(name = "id") final Long id) {
        tlgNotificationService.delete(id);
        return ResponseEntity.noContent().build();
    }

}