package apps.juice_up.repos;

import apps.juice_up.domain.Scope;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ScopeRepository extends JpaRepository<Scope, Long> {
}
