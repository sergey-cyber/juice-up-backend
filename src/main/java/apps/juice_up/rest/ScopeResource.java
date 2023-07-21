package apps.juice_up.rest;

import apps.juice_up.model.JwtUserDetails;
import apps.juice_up.model.ScopeDTO;
import apps.juice_up.service.ScopeService;
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
@RequestMapping(value = "/api/scopes", produces = MediaType.APPLICATION_JSON_VALUE)
public class ScopeResource {

    private final ScopeService scopeService;

    public ScopeResource(final ScopeService scopeService) {
        this.scopeService = scopeService;
    }

    @GetMapping
    public ResponseEntity<List<ScopeDTO>> getAllScopes(@AuthenticationPrincipal JwtUserDetails principal) {
        return ResponseEntity.ok(scopeService.findByUserId(principal.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScopeDTO> getScope(
            @PathVariable(name = "id") final Long id, @AuthenticationPrincipal JwtUserDetails principal) {
        var scope = scopeService.get(id);
        if(!Objects.equals(scope.getUser(), principal.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(scope);
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createScope(
            @RequestBody @Valid final ScopeDTO scopeDTO, @AuthenticationPrincipal JwtUserDetails principal) {
        scopeDTO.setUser(principal.getId());
        final Long createdId = scopeService.create(scopeDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateScope(
            @PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ScopeDTO scopeDTO,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {
        var scope = scopeService.get(id);
        if(!Objects.equals(scope.getUser(), principal.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        scopeService.update(id, scopeDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteScope(
            @PathVariable(name = "id") final Long id, @AuthenticationPrincipal JwtUserDetails principal) {
        var scope = scopeService.get(id);
        if(!Objects.equals(scope.getUser(), principal.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        scopeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
