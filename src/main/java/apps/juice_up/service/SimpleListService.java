package apps.juice_up.service;

import apps.juice_up.domain.SimpleList;
import apps.juice_up.domain.User;
import apps.juice_up.model.SimpleListDTO;
import apps.juice_up.repos.SimpleListRepository;
import apps.juice_up.repos.UserRepository;
import apps.juice_up.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class SimpleListService {

    private final SimpleListRepository simpleListRepository;
    private final UserRepository userRepository;

    public SimpleListService(final SimpleListRepository simpleListRepository,
            final UserRepository userRepository) {
        this.simpleListRepository = simpleListRepository;
        this.userRepository = userRepository;
    }

    public List<SimpleListDTO> findAll() {
        final List<SimpleList> simpleLists = simpleListRepository.findAll(Sort.by("id"));
        return simpleLists.stream()
                .map((simpleList) -> mapToDTO(simpleList, new SimpleListDTO()))
                .toList();
    }

    public SimpleListDTO get(final Long id) {
        return simpleListRepository.findById(id)
                .map((simpleList) -> mapToDTO(simpleList, new SimpleListDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SimpleListDTO simpleListDTO) {
        final SimpleList simpleList = new SimpleList();
        mapToEntity(simpleListDTO, simpleList);
        return simpleListRepository.save(simpleList).getId();
    }

    public void update(final Long id, final SimpleListDTO simpleListDTO) {
        final SimpleList simpleList = simpleListRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(simpleListDTO, simpleList);
        simpleListRepository.save(simpleList);
    }

    public void delete(final Long id) {
        simpleListRepository.deleteById(id);
    }

    private SimpleListDTO mapToDTO(final SimpleList simpleList, final SimpleListDTO simpleListDTO) {
        simpleListDTO.setId(simpleList.getId());
        simpleListDTO.setName(simpleList.getName());
        simpleListDTO.setUser(simpleList.getUser() == null ? null : simpleList.getUser().getId());
        return simpleListDTO;
    }

    private SimpleList mapToEntity(final SimpleListDTO simpleListDTO, final SimpleList simpleList) {
        simpleList.setName(simpleListDTO.getName());
        final User user = simpleListDTO.getUser() == null ? null : userRepository.findById(simpleListDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        simpleList.setUser(user);
        return simpleList;
    }

}
