package apps.juice_up.repos;

import apps.juice_up.domain.SimpleList;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SimpleListRepository extends JpaRepository<SimpleList, Long> {
}
