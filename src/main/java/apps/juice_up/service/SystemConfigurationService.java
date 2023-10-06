package apps.juice_up.service;

import apps.juice_up.domain.SystemConfiguration;
import apps.juice_up.domain.User;
import apps.juice_up.model.SystemConfigurationDTO;
import apps.juice_up.repos.SystemConfigurationRepository;
import apps.juice_up.repos.UserRepository;
import apps.juice_up.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class SystemConfigurationService {

    private final SystemConfigurationRepository systemConfigurationRepository;
    private final UserRepository userRepository;
    //private final OverdueTodosPolicyRepository overdueTodosPolicyRepository;

    public SystemConfigurationService(
            final SystemConfigurationRepository systemConfigurationRepository,
            final UserRepository userRepository) {
        this.systemConfigurationRepository = systemConfigurationRepository;
        this.userRepository = userRepository;
    }

    public List<SystemConfigurationDTO> findAll() {
        final List<SystemConfiguration> systemConfigurations = systemConfigurationRepository.findAll(Sort.by("id"));
        return systemConfigurations.stream()
                .map(systemConfiguration -> mapToDTO(systemConfiguration, new SystemConfigurationDTO()))
                .toList();
    }

    public SystemConfigurationDTO get(final Long id) {
        return systemConfigurationRepository.findById(id)
                .map(systemConfiguration -> mapToDTO(systemConfiguration, new SystemConfigurationDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public SystemConfigurationDTO findByUserId(final Long userId) {
        var systemConfig = systemConfigurationRepository.findByUserId(userId);
        if(systemConfig == null) {
            throw new NotFoundException();
        }
        return mapToDTO(systemConfig, new SystemConfigurationDTO());
    }

    public Long create(final SystemConfigurationDTO systemConfigurationDTO) {
        final SystemConfiguration systemConfiguration = new SystemConfiguration();
        mapToEntity(systemConfigurationDTO, systemConfiguration);
        return systemConfigurationRepository.save(systemConfiguration).getId();
    }

    public void update(final Long id, final SystemConfigurationDTO systemConfigurationDTO) {
        final SystemConfiguration systemConfiguration = systemConfigurationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(systemConfigurationDTO, systemConfiguration);
        systemConfigurationRepository.save(systemConfiguration);
    }

    public void delete(final Long id) {
        systemConfigurationRepository.deleteById(id);
    }

    private SystemConfigurationDTO mapToDTO(final SystemConfiguration systemConfiguration,
                                            final SystemConfigurationDTO systemConfigurationDTO) {
        systemConfigurationDTO.setId(systemConfiguration.getId());
        systemConfigurationDTO.setUser(systemConfiguration.getUser() == null ? null : systemConfiguration.getUser().getId());
        systemConfigurationDTO.setOverdueTodosPolicy(systemConfiguration.getOverdueTodosPolicy());
        systemConfigurationDTO.setTestEntityField(systemConfiguration.getTestEntityField());
        return systemConfigurationDTO;
    }

    private SystemConfiguration mapToEntity(final SystemConfigurationDTO systemConfigurationDTO,
                                            final SystemConfiguration systemConfiguration) {
        final User user = systemConfigurationDTO.getUser() == null ? null : userRepository.findById(systemConfigurationDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        systemConfiguration.setUser(user);
        systemConfiguration.setOverdueTodosPolicy(systemConfigurationDTO.getOverdueTodosPolicy());
        systemConfiguration.setTestEntityField(systemConfigurationDTO.getTestEntityField());
        return systemConfiguration;
    }

}