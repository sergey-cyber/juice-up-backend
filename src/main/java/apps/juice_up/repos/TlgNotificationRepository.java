package apps.juice_up.repos;

import apps.juice_up.domain.TlgNotification;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TlgNotificationRepository extends JpaRepository<TlgNotification, Long> {
}