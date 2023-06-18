package apps.juice_up.repos;

import apps.juice_up.domain.SimpleListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SimpleListItemRepository extends JpaRepository<SimpleListItem, Long> {
    public List<SimpleListItem> findBySimpleListId(Long id);
}
