package apps.juice_up.rest;

import apps.juice_up.bot.components.BotSkills;
import apps.juice_up.model.JwtUserDetails;
import apps.juice_up.model.TlgNotificationDTO;
import apps.juice_up.service.TlgNotificationService;
import apps.juice_up.service.TodoService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.POST})
@RestController
@RequestMapping(value = "/api/tlgNotifications", produces = MediaType.APPLICATION_JSON_VALUE)
@PropertySource("bot.properties")
public class TlgNotificationResource {

    private final TlgNotificationService tlgNotificationService;
    private final TodoService todoService;
    private final BotSkills botSkills;

    public TlgNotificationResource(final TlgNotificationService tlgNotificationService, TodoService todoService, BotSkills botSkills) {
        this.tlgNotificationService = tlgNotificationService;
        this.todoService = todoService;
        this.botSkills = botSkills;
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
            @AuthenticationPrincipal JwtUserDetails principal,
            @Value("${bot.name}") String botName
    ) {
        if(principal.getTelegramId() == null) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "You are not subscribed to the notification mailing list. To fix this send a message to the " + botName
            );
        }

        var currentDate = new Date();
        System.out.println("Current: " + currentDate);
        if (tlgNotificationDTO.getExecuteTimestamp().before(currentDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Specified time is after current time"
            );
        }

        var todoDto = todoService.get(tlgNotificationDTO.getTodo());
        tlgNotificationDTO.setRecipientId(principal.getTelegramId());
        var extraMessage = tlgNotificationDTO.getMessage() != null ? tlgNotificationDTO.getMessage() : "";
        tlgNotificationDTO.setMessage(todoDto.getName() + "; " + extraMessage);
        final Long createdId = tlgNotificationService.create(tlgNotificationDTO);
        todoDto.setTlgNotification(createdId);
        todoService.update(todoDto.getId(), todoDto);
        botSkills.sendMessageToSpecificTime(tlgNotificationDTO, principal.getTelegramId());
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