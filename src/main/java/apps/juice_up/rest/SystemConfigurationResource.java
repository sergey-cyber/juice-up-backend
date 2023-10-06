package apps.juice_up.rest;

import apps.juice_up.model.JwtUserDetails;
import apps.juice_up.model.SystemConfigurationDTO;
import apps.juice_up.service.SystemConfigurationService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.POST})
@RestController
@RequestMapping(value = "/api/systemConfigurations", produces = MediaType.APPLICATION_JSON_VALUE)
public class SystemConfigurationResource {

    private final SystemConfigurationService systemConfigurationService;

    public SystemConfigurationResource(
            final SystemConfigurationService systemConfigurationService) {
        this.systemConfigurationService = systemConfigurationService;
    }

    @GetMapping("/")
    public ResponseEntity<SystemConfigurationDTO> getSystemConfigurationsByPrincipal(@AuthenticationPrincipal JwtUserDetails principal) {
        return ResponseEntity.ok(systemConfigurationService.findByUserId(principal.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SystemConfigurationDTO> getSystemConfiguration(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(systemConfigurationService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createSystemConfiguration(
            @RequestBody @Valid final SystemConfigurationDTO systemConfigurationDTO) {
        final Long createdId = systemConfigurationService.create(systemConfigurationDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateSystemConfiguration(@PathVariable(name = "id") final Long id,
                                                          @RequestBody @Valid final SystemConfigurationDTO systemConfigurationDTO) {
        systemConfigurationService.update(id, systemConfigurationDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSystemConfiguration(
            @PathVariable(name = "id") final Long id) {
        systemConfigurationService.delete(id);
        return ResponseEntity.noContent().build();
    }

}