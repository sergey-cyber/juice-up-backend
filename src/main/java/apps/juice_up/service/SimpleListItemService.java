package apps.juice_up.service;

import apps.juice_up.domain.SimpleList;
import apps.juice_up.domain.SimpleListItem;
import apps.juice_up.model.SimpleListItemDTO;
import apps.juice_up.repos.SimpleListItemRepository;
import apps.juice_up.repos.SimpleListRepository;
import apps.juice_up.util.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class SimpleListItemService {

    private final SimpleListItemRepository simpleListItemRepository;
    private final SimpleListRepository simpleListRepository;

    public SimpleListItemService(final SimpleListItemRepository simpleListItemRepository,
            final SimpleListRepository simpleListRepository) {
        this.simpleListItemRepository = simpleListItemRepository;
        this.simpleListRepository = simpleListRepository;
    }

    public List<SimpleListItemDTO> findAll() {
        final List<SimpleListItem> simpleListItems = simpleListItemRepository.findAll(Sort.by("id"));
        return simpleListItems.stream()
                .map((simpleListItem) -> mapToDTO(simpleListItem, new SimpleListItemDTO()))
                .toList();
    }

    public List<SimpleListItemDTO> findByListId(Long id) {
        final List<SimpleListItem> simpleListItems = simpleListItemRepository.findBySimpleListId(id);
        return simpleListItems.stream()
                .map((simpleListItem) -> mapToDTO(simpleListItem, new SimpleListItemDTO()))
                .toList();
    }

    public SimpleListItemDTO get(final Long id) {
        return simpleListItemRepository.findById(id)
                .map((simpleListItem) -> mapToDTO(simpleListItem, new SimpleListItemDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SimpleListItemDTO simpleListItemDTO) {
        final SimpleListItem simpleListItem = new SimpleListItem();
        mapToEntity(simpleListItemDTO, simpleListItem);
        return simpleListItemRepository.save(simpleListItem).getId();
    }

    public void update(final Long id, final SimpleListItemDTO simpleListItemDTO) {
        final SimpleListItem simpleListItem = simpleListItemRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(simpleListItemDTO, simpleListItem);
        simpleListItemRepository.save(simpleListItem);
    }

    public void delete(final Long id) {
        simpleListItemRepository.deleteById(id);
    }

    private SimpleListItemDTO mapToDTO(final SimpleListItem simpleListItem,
            final SimpleListItemDTO simpleListItemDTO) {
        simpleListItemDTO.setId(simpleListItem.getId());
        simpleListItemDTO.setName(simpleListItem.getName());
        simpleListItemDTO.setSimpleList(simpleListItem.getSimpleList() == null ? null : simpleListItem.getSimpleList().getId());
        return simpleListItemDTO;
    }

    private SimpleListItem mapToEntity(final SimpleListItemDTO simpleListItemDTO,
            final SimpleListItem simpleListItem) {
        simpleListItem.setName(simpleListItemDTO.getName());
        final SimpleList simpleList = simpleListItemDTO.getSimpleList() == null ? null : simpleListRepository.findById(simpleListItemDTO.getSimpleList())
                .orElseThrow(() -> new NotFoundException("simpleList not found"));
        simpleListItem.setSimpleList(simpleList);
        return simpleListItem;
    }

}
