package apps.juice_up.rest;

import apps.juice_up.model.*;
import apps.juice_up.service.JwtTokenService;
import apps.juice_up.service.JwtUserDetailsService;
import apps.juice_up.service.SystemConfigurationService;
import apps.juice_up.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
@CrossOrigin(methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.POST})
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthResource {
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final SystemConfigurationService systemConfigurationService;


    public AuthResource(final JwtUserDetailsService jwtUserDetailsService, final JwtTokenService jwtTokenService, AuthenticationManager authenticationManager, UserService userService, SystemConfigurationService systemConfigurationService) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.systemConfigurationService = systemConfigurationService;
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthResponse>  authenticate(@RequestBody @Valid final AuthRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getLogin(), authenticationRequest.getPassword()));
        } catch (final BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(authenticationRequest.getLogin());
        final AuthResponse authenticationResponse = new AuthResponse();
        authenticationResponse.setAccessToken(jwtTokenService.generateToken(userDetails));
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/registration")
    public ResponseEntity<Long> registration(@RequestBody final UserDTO userDTO) {
        if(userService.isAlreadyExists(userDTO.getName())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, String.format("User with name %s already exists.", userDTO.getName())
            );
        }
        var new_user_id = userService.create(userDTO);
        var systemConfiguration = new SystemConfigurationDTO();
        systemConfiguration.setUser(new_user_id);
        systemConfigurationService.create(systemConfiguration);
        return new ResponseEntity<>(new_user_id, HttpStatus.CREATED);
    }

    @GetMapping("/who-am-i")
    public ResponseEntity<?> whoAmI(@AuthenticationPrincipal JwtUserDetails principal) {
        //TODO: need normal DTO
        var user = Map.of(
                "name", principal.getUsername(),
                "id", principal.getId()
        );
        return ResponseEntity.ok(user);
    }
}
