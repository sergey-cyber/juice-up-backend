package apps.juice_up.repos;

import apps.juice_up.domain.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {
    SystemConfiguration findByUserId(Long id);
}
