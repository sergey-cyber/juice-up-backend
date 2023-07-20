package apps.juice_up.service;

import apps.juice_up.domain.Scope;
import apps.juice_up.domain.User;
import apps.juice_up.model.ScopeDTO;
import apps.juice_up.repos.ScopeRepository;
import apps.juice_up.repos.UserRepository;
import apps.juice_up.util.NotFoundException;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ScopeService {

    private final ScopeRepository scopeRepository;
    private final UserRepository userRepository;

    public ScopeService(final ScopeRepository scopeRepository,
            final UserRepository userRepository) {
        this.scopeRepository = scopeRepository;
        this.userRepository = userRepository;
    }

    public List<ScopeDTO> findAll() {
        final List<Scope> scopes = scopeRepository.findAll(Sort.by("id"));
        return scopes.stream()
                .map((scope) -> mapToDTO(scope, new ScopeDTO()))
                .toList();
    }

    public List<ScopeDTO> findByUserId(Long id) {
        final List<Scope> scopes = scopeRepository.findByUserId(id);
        return scopes.stream()
                .map((scope) -> mapToDTO(scope, new ScopeDTO()))
                .toList();
    }

    public ScopeDTO get(final Long id) {
        var scope = scopeRepository.findById(id);
        return scope
                .map((s) -> mapToDTO(s, new ScopeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ScopeDTO scopeDTO) {
        final Scope scope = new Scope();
        mapToEntity(scopeDTO, scope);
        return scopeRepository.save(scope).getId();
    }

    public void update(final Long id, final ScopeDTO scopeDTO) {
        final Scope scope = scopeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(scopeDTO, scope);
        scopeRepository.save(scope);
    }

    public void delete(final Long id) {
        scopeRepository.deleteById(id);
    }

    private ScopeDTO mapToDTO(final Scope scope, final ScopeDTO scopeDTO) {
        scopeDTO.setId(scope.getId());
        scopeDTO.setName(scope.getName());
        scopeDTO.setUser(scope.getUser() == null ? null : scope.getUser().getId());
        //scopeDTO.setTodos(scope.getTodos());
        return scopeDTO;
    }

    private Scope mapToEntity(final ScopeDTO scopeDTO, final Scope scope) {
        scope.setName(scopeDTO.getName());
        final User user = scopeDTO.getUser() == null ? null : userRepository.findById(scopeDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        scope.setUser(user);
        return scope;
    }

}
