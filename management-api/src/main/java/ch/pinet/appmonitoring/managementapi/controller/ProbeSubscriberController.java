package ch.pinet.appmonitoring.managementapi.controller;

import ch.pinet.appmonitoring.managementapi.entities.ProbeSubscriber;
import ch.pinet.appmonitoring.managementapi.repository.ProbeSubscriberRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/probe-subscribers")
@CrossOrigin(origins = "*", exposedHeaders = "X-Total-Count")
public class ProbeSubscriberController {

    private final ProbeSubscriberRepository repository;

    public ProbeSubscriberController(ProbeSubscriberRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<ProbeSubscriber>> getAll(
            @RequestParam Optional<Integer> _page,
            @RequestParam Optional<Integer> _limit,
            @RequestParam Optional<String> _sort,
            @RequestParam Optional<String> _order,
            @RequestParam Map<String, String> allRequestParams
    ) {
        int page = _page.orElse(1) - 1;
        int limit = _limit.orElse(10);
        String sortField = _sort.orElse("id");
        Sort.Direction direction = _order.map(String::toUpperCase)
                .map(Sort.Direction::valueOf)
                .orElse(Sort.Direction.ASC);
        Pageable pageable = PageRequest.of(page, limit, Sort.by(direction, sortField));

        Specification<ProbeSubscriber> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (allRequestParams.containsKey("probeId")) {
                try {
                    UUID probeId = UUID.fromString(allRequestParams.get("probeId"));
                    predicate = cb.and(predicate, cb.equal(root.get("probeId"), probeId));
                } catch (IllegalArgumentException ignored) {}
            }

            if (allRequestParams.containsKey("subscriberId")) {
                try {
                    UUID subscriberId = UUID.fromString(allRequestParams.get("subscriberId"));
                    predicate = cb.and(predicate, cb.equal(root.get("subscriberId"), subscriberId));
                } catch (IllegalArgumentException ignored) {}
            }

            return predicate;
        };

        Page<ProbeSubscriber> result = repository.findAll(spec, pageable);
        long total = result.getTotalElements();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(total));

        return new ResponseEntity<>(result.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ProbeSubscriber get(@PathVariable UUID id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping
    public ProbeSubscriber create(@RequestBody ProbeSubscriber entity) {
        return repository.save(entity);
    }

    @PutMapping("/{id}")
    public ProbeSubscriber update(@PathVariable UUID id, @RequestBody ProbeSubscriber entity) {
        entity.setId(id);
        return repository.save(entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        repository.deleteById(id);
    }
}
