package apps.juice_up.rest;

import apps.juice_up.model.JwtUserDetails;
import apps.juice_up.model.TodoDTO;
import apps.juice_up.service.ScopeService;
import apps.juice_up.service.TodoService;
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
@RequestMapping(value = "/api/todos", produces = MediaType.APPLICATION_JSON_VALUE)
public class TodoResource {

    private final TodoService todoService;
    private final ScopeService scopeService;

    public TodoResource(final TodoService todoService, ScopeService scopeService) {
        this.todoService = todoService;
        this.scopeService = scopeService;
    }

    @GetMapping
    public ResponseEntity<List<TodoDTO>> getAllTodos(@AuthenticationPrincipal JwtUserDetails principal) {
        // getting todos for authenticated user
        return ResponseEntity.ok(todoService.findByUserId(principal.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDTO> getTodo(
            @PathVariable(name = "id") final Long id, @AuthenticationPrincipal JwtUserDetails principal) {
        var todo = todoService.get(id);
        if(!Objects.equals(todo.getUser(), principal.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(todo);
    }

    // TODO: лучше сделать метод search и в requestBody отправлять query-filter
    @GetMapping(value = "/by-day/{day}")
    public ResponseEntity<List<TodoDTO>> getByDay(
            @PathVariable(name = "day") String day, @AuthenticationPrincipal JwtUserDetails principal) {
        final List<TodoDTO> todos = todoService.getByDay(principal.getId(), day);

        return ResponseEntity.ok(todos);
    }

    @GetMapping(value = "/by-scopeId/{id}")
    public ResponseEntity<List<TodoDTO>> searchByScopeId(
            @PathVariable(name = "id") Long id, @AuthenticationPrincipal JwtUserDetails principal) {
        var scope = scopeService.get(id);
        if(!Objects.equals(scope.getUser(), principal.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(todoService.searchByScopeId(principal.getId(), id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createTodo(
            @RequestBody @Valid final TodoDTO todoDTO, @AuthenticationPrincipal JwtUserDetails principal) {
        todoDTO.setUser(principal.getId());
        final Long createdId = todoService.create(todoDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTodo(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final TodoDTO todoDTO, @AuthenticationPrincipal JwtUserDetails principal) {
        var todo = todoService.get(id);
        if(!Objects.equals(todo.getUser(), principal.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        todoService.update(id, todoDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteTodo(
            @PathVariable(name = "id") final Long id, @AuthenticationPrincipal JwtUserDetails principal) {
        var todo = todoService.get(id);
        if(!Objects.equals(todo.getUser(), principal.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/in-month/{year_month}")
    public ResponseEntity<List<TodoDTO>> getTodosByMonth(
            @PathVariable(name = "year_month") String year_month,
            @AuthenticationPrincipal JwtUserDetails principal
    ) {

        // year_month = yyyy:mm
        final List<TodoDTO> todosDto = todoService.getByMonth(principal.getId(), year_month);

        return todosDto != null
                ? new ResponseEntity<>(todosDto, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
