package apps.juice_up.service;

import apps.juice_up.domain.TlgNotification;
import apps.juice_up.domain.Todo;
import apps.juice_up.model.TlgNotificationDTO;
import apps.juice_up.repos.TlgNotificationRepository;
import apps.juice_up.repos.TodoRepository;
import apps.juice_up.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class TlgNotificationService {

    private final TlgNotificationRepository tlgNotificationRepository;
    private final TodoRepository todoRepository;

    public TlgNotificationService(final TlgNotificationRepository tlgNotificationRepository,
                                  final TodoRepository todoRepository) {
        this.tlgNotificationRepository = tlgNotificationRepository;
        this.todoRepository = todoRepository;
    }

    public List<TlgNotificationDTO> findAll() {
        final List<TlgNotification> tlgNotifications = tlgNotificationRepository.findAll(Sort.by("id"));
        return tlgNotifications.stream()
                .map(tlgNotification -> mapToDTO(tlgNotification, new TlgNotificationDTO()))
                .toList();
    }

    public TlgNotificationDTO get(final Long id) {
        return tlgNotificationRepository.findById(id)
                .map(tlgNotification -> mapToDTO(tlgNotification, new TlgNotificationDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final TlgNotificationDTO tlgNotificationDTO) {
        final TlgNotification tlgNotification = new TlgNotification();
        mapToEntity(tlgNotificationDTO, tlgNotification);
        return tlgNotificationRepository.save(tlgNotification).getId();
    }

    public void update(final Long id, final TlgNotificationDTO tlgNotificationDTO) {
        final TlgNotification tlgNotification = tlgNotificationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(tlgNotificationDTO, tlgNotification);
        tlgNotificationRepository.save(tlgNotification);
    }

    public void delete(final Long id) {
        tlgNotificationRepository.deleteById(id);
    }

    private TlgNotificationDTO mapToDTO(final TlgNotification tlgNotification,
                                        final TlgNotificationDTO tlgNotificationDTO) {
        tlgNotificationDTO.setId(tlgNotification.getId());
        tlgNotificationDTO.setExecuteTimestamp(tlgNotification.getExecuteTimestamp());
        tlgNotificationDTO.setMessage(tlgNotification.getMessage());
        tlgNotificationDTO.setRecipientId(tlgNotification.getRecipientId());
        tlgNotificationDTO.setTodo(tlgNotification.getTodo() == null ? null : tlgNotification.getTodo().getId());
        return tlgNotificationDTO;
    }

    private TlgNotification mapToEntity(final TlgNotificationDTO tlgNotificationDTO,
                                        final TlgNotification tlgNotification) {
        tlgNotification.setExecuteTimestamp(tlgNotificationDTO.getExecuteTimestamp());
        tlgNotification.setMessage(tlgNotificationDTO.getMessage());
        tlgNotification.setRecipientId(tlgNotificationDTO.getRecipientId());
        final Todo todo = tlgNotificationDTO.getTodo() == null ? null : todoRepository.findById(tlgNotificationDTO.getTodo())
                .orElseThrow(() -> new NotFoundException("todo not found"));
        tlgNotification.setTodo(todo);
        return tlgNotification;
    }

}
