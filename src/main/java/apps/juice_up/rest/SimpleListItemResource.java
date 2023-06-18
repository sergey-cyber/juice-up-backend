package apps.juice_up.rest;

import apps.juice_up.model.SimpleListItemDTO;
import apps.juice_up.service.SimpleListItemService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.POST})
@RestController
@RequestMapping(value = "/api/simpleListItems", produces = MediaType.APPLICATION_JSON_VALUE)
public class SimpleListItemResource {

    private final SimpleListItemService simpleListItemService;

    public SimpleListItemResource(final SimpleListItemService simpleListItemService) {
        this.simpleListItemService = simpleListItemService;
    }

    @GetMapping
    public ResponseEntity<List<SimpleListItemDTO>> getAllSimpleListItems() {
        return ResponseEntity.ok(simpleListItemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SimpleListItemDTO> getSimpleListItem(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(simpleListItemService.get(id));
    }

    @GetMapping("/searchByListId/{id}")
    public ResponseEntity<List<SimpleListItemDTO>> searchByListId(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(simpleListItemService.findByListId(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createSimpleListItem(
            @RequestBody @Valid final SimpleListItemDTO simpleListItemDTO) {
        final Long createdId = simpleListItemService.create(simpleListItemDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/createItems")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<List<Long>> createSimpleListItems(
            @RequestBody @Valid final List<SimpleListItemDTO> simpleListItemsDTO) {
        List<Long> created = new ArrayList<>();
        for(var item : simpleListItemsDTO) {
            Long createdId = simpleListItemService.create(item);
            created.add(createdId);
        }
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateSimpleListItem(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final SimpleListItemDTO simpleListItemDTO) {
        simpleListItemService.update(id, simpleListItemDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSimpleListItem(@PathVariable(name = "id") final Long id) {
        simpleListItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
