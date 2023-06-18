package apps.juice_up.repos;

import apps.juice_up.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;


public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByDayEquals(String day);

    List<Todo> findByScopeId(Long scope);
}
