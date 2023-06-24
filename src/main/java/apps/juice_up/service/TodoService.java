package apps.juice_up.service;

import apps.juice_up.domain.Scope;
import apps.juice_up.domain.Todo;
import apps.juice_up.domain.User;
import apps.juice_up.model.TodoDTO;
import apps.juice_up.repos.ScopeRepository;
import apps.juice_up.repos.TodoRepository;
import apps.juice_up.repos.UserRepository;
import apps.juice_up.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final ScopeRepository scopeRepository;
    private final UserRepository userRepository;

    public TodoService(final TodoRepository todoRepository, final ScopeRepository scopeRepository,
            final UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.scopeRepository = scopeRepository;
        this.userRepository = userRepository;
    }

    public List<TodoDTO> findAll() {
        final List<Todo> todos = todoRepository.findAll(Sort.by("id"));
        return todos.stream()
                .map((todo) -> mapToDTO(todo, new TodoDTO()))
                .toList();
    }

    public TodoDTO get(final Long id) {
        return todoRepository.findById(id)
                .map((todo) -> mapToDTO(todo, new TodoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final TodoDTO todoDTO) {
        final Todo todo = new Todo();
        mapToEntity(todoDTO, todo);
        return todoRepository.save(todo).getId();
    }

    public void update(final Long id, final TodoDTO todoDTO) {
        final Todo todo = todoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(todoDTO, todo);
        todoRepository.save(todo);
    }

    public void delete(final Long id) {
        todoRepository.deleteById(id);
    }

    public List<TodoDTO> getByDay(String day) {
        final List<Todo> todos = todoRepository.findByDayEquals(day);
        return todos.stream()
                .map((todo) -> mapToDTO(todo, new TodoDTO()))
                .toList();
    }

    public List<TodoDTO> searchByScopeId(Long id) {
        final List<Todo> todos = todoRepository.findByScopeId(id);
        return todos.stream()
                .map((todo) -> mapToDTO(todo, new TodoDTO()))
                .toList();
    }

    public List<TodoDTO> getByMonth(String day) {
        // day format = yyyy:mm
        var todos = todoRepository.findByDayStartingWith(day);
        return todos.stream()
                .map((todo) -> mapToDTO(todo, new TodoDTO()))
                .toList();
    }

    private TodoDTO mapToDTO(final Todo todo, final TodoDTO todoDTO) {
        todoDTO.setId(todo.getId());
        todoDTO.setName(todo.getName());
        todoDTO.setStatus(todo.getStatus());
        todoDTO.setDay(todo.getDay());
        todoDTO.setDescription(todo.getDescription());
        todoDTO.setScope(todo.getScope() == null ? null : todo.getScope().getId());
        todoDTO.setUser(todo.getUser() == null ? null : todo.getUser().getId());
        return todoDTO;
    }

    private Todo mapToEntity(final TodoDTO todoDTO, final Todo todo) {
        todo.setName(todoDTO.getName());
        todo.setStatus(todoDTO.getStatus());
        todo.setDay(todoDTO.getDay());
        todo.setDescription(todoDTO.getDescription());
        final Scope scope = todoDTO.getScope() == null ? null : scopeRepository.findById(todoDTO.getScope())
                .orElseThrow(() -> new NotFoundException("scope not found"));
        todo.setScope(scope);
        final User user = todoDTO.getUser() == null ? null : userRepository.findById(todoDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        todo.setUser(user);
        return todo;
    }

}
