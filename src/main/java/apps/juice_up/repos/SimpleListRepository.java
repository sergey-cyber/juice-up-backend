package apps.juice_up.repos;

import apps.juice_up.domain.SimpleList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SimpleListRepository extends JpaRepository<SimpleList, Long> {
    List<SimpleList> findByUserId(Long id);
}
