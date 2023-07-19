package apps.juice_up.repos;

import apps.juice_up.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByDayEquals(String day);
    List<Todo> findByUserIdAndDayEquals(Long id, String day);

    List<Todo> findByScopeId(Long scope);
    List<Todo> findByUserIdAndScopeId(Long userId, Long scope);
    List<Todo> findByUserId(Long user);

    List<Todo> findByDayStartingWith(String prefix);
    List<Todo> findByUserIdAndDayStartingWith(Long id, String prefix);
}
