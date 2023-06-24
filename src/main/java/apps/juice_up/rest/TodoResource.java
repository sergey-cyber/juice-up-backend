package apps.juice_up.rest;

import apps.juice_up.model.TodoDTO;
import apps.juice_up.service.TodoService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.POST})
@RestController
@RequestMapping(value = "/api/todos", produces = MediaType.APPLICATION_JSON_VALUE)
public class TodoResource {

    private final TodoService todoService;

    public TodoResource(final TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<TodoDTO>> getAllTodos() {
        return ResponseEntity.ok(todoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDTO> getTodo(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(todoService.get(id));
    }

    // TODO: лучше сделать метод search и в requestBody отправлять query-filter
    @GetMapping(value = "/by-day/{day}")
    public ResponseEntity<List<TodoDTO>> getByDay(@PathVariable(name = "day") String day) {
        final List<TodoDTO> todos = todoService.getByDay(day);

        return ResponseEntity.ok(todos);
    }

    @GetMapping(value = "/by-scopeId/{id}")
    public ResponseEntity<List<TodoDTO>> searchByScopeId(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(todoService.searchByScopeId(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createTodo(@RequestBody @Valid final TodoDTO todoDTO) {
        final Long createdId = todoService.create(todoDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTodo(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final TodoDTO todoDTO) {
        todoService.update(id, todoDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteTodo(@PathVariable(name = "id") final Long id) {
        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/in-month/{year_month}")
    public ResponseEntity<List<TodoDTO>> getTodosByMonth(@PathVariable(name = "year_month") String year_month) {

        // year_month = yyyy:mm
        final List<TodoDTO> todosDto = todoService.getByMonth(year_month);

        return todosDto != null
                ? new ResponseEntity<>(todosDto, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
