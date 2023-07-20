package apps.juice_up.repos;

import apps.juice_up.domain.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ScopeRepository extends JpaRepository<Scope, Long> {
    List<Scope> findByUserId(Long user);
}
