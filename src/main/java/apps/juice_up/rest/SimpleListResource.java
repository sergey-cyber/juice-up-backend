package apps.juice_up.rest;

import apps.juice_up.model.JwtUserDetails;
import apps.juice_up.model.SimpleListDTO;
import apps.juice_up.service.SimpleListService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.POST})
@RestController
@RequestMapping(value = "/api/simpleLists", produces = MediaType.APPLICATION_JSON_VALUE)
public class SimpleListResource {

    private final SimpleListService simpleListService;

    public SimpleListResource(final SimpleListService simpleListService) {
        this.simpleListService = simpleListService;
    }

    @GetMapping
    public ResponseEntity<List<SimpleListDTO>> getAllSimpleLists(@AuthenticationPrincipal JwtUserDetails principal) {
        return ResponseEntity.ok(simpleListService.findByUserId(principal.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SimpleListDTO> getSimpleList(
            @PathVariable(name = "id") final Long id, @AuthenticationPrincipal JwtUserDetails principal) {
        var simpleList = simpleListService.get(id);
        if(!Objects.equals(simpleList.getUser(), principal.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(simpleList);
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createSimpleList(
            @RequestBody @Valid final SimpleListDTO simpleListDTO) {
        final Long createdId = simpleListService.create(simpleListDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateSimpleList(
            @PathVariable(name = "id") final Long id,
            @RequestBody @Valid final SimpleListDTO simpleListDTO,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        var simpleList = simpleListService.get(id);
        if(!Objects.equals(simpleList.getUser(), principal.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        simpleListService.update(id, simpleListDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSimpleList(
            @PathVariable(name = "id") final Long id, @AuthenticationPrincipal JwtUserDetails principal) {
        var simpleList = simpleListService.get(id);
        if(!Objects.equals(simpleList.getUser(), principal.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        simpleListService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
