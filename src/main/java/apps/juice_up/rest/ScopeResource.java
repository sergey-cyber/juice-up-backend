package apps.juice_up.rest;

import apps.juice_up.model.ScopeDTO;
import apps.juice_up.service.ScopeService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.POST})
@RestController
@RequestMapping(value = "/api/scopes", produces = MediaType.APPLICATION_JSON_VALUE)
public class ScopeResource {

    private final ScopeService scopeService;

    public ScopeResource(final ScopeService scopeService) {
        this.scopeService = scopeService;
    }

    @GetMapping
    public ResponseEntity<List<ScopeDTO>> getAllScopes() {
        return ResponseEntity.ok(scopeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScopeDTO> getScope(@PathVariable(name = "id") final Long id) {
        var scope = scopeService.get(id);
        return ResponseEntity.ok(scope);
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createScope(@RequestBody @Valid final ScopeDTO scopeDTO) {
        final Long createdId = scopeService.create(scopeDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateScope(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ScopeDTO scopeDTO) {
        scopeService.update(id, scopeDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteScope(@PathVariable(name = "id") final Long id) {
        scopeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
