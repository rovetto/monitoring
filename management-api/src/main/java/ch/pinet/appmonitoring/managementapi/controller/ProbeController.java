package ch.pinet.appmonitoring.managementapi.controller;

import ch.pinet.appmonitoring.managementapi.entities.Probe;
import ch.pinet.appmonitoring.managementapi.repository.ProbeRepository;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/probes")
@CrossOrigin(origins = "*", exposedHeaders = "X-Total-Count")
public class ProbeController {

    private final ProbeRepository probeRepository;

    public ProbeController(ProbeRepository probeRepository) {
        this.probeRepository = probeRepository;
    }

    @GetMapping
    public ResponseEntity<List<Probe>> getProbes(
            @RequestParam Optional<Integer> _page,
            @RequestParam Optional<Integer> _limit,
            @RequestParam Optional<String> _sort,
            @RequestParam Optional<String> _order,
            @RequestParam(required = false) String q,
            @RequestParam Map<String, String> allRequestParams
    ) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Params: _page={}, _limit={}, _sort={}, _order={}, q={}, allRequestParams={}",
                _page, _limit, _sort, _order, q, allRequestParams);

        int page = _page.orElse(1) - 1;
        int limit = _limit.orElse(10);
        String sortField = _sort.orElse("id");
        Sort.Direction direction = _order.map(String::toUpperCase).map(Sort.Direction::valueOf).orElse(Sort.Direction.ASC);
        Pageable pageable = PageRequest.of(page, limit, Sort.by(direction, sortField));

        Specification<Probe> spec = (root, query, cb) -> {
            final Predicate[] predicate = {cb.conjunction()}; // Array, um Änderungen zu ermöglichen

            // Freitextsuche für Autocomplete (über name)
            if (q != null && !q.isEmpty()) {
                String searchPattern = "%" + q.toLowerCase() + "%";
                predicate[0] = cb.and(predicate[0], cb.like(cb.lower(root.get("name")), searchPattern));
            }

            // Filter für ReferenceField (probeId)
            if (allRequestParams.containsKey("probeId")) {
                try {
                    UUID probeId = UUID.fromString(allRequestParams.get("probeId"));
                    predicate[0] = cb.and(predicate[0], cb.equal(root.get("id"), probeId));
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid UUID format for probeId: {}", allRequestParams.get("probeId"), e);
                    throw new RuntimeException("Invalid UUID format for probeId: " + allRequestParams.get("probeId"), e);
                }
            }

            // Weitere flache Parameter (z. B. name)
            allRequestParams.forEach((key, value) -> {
                if (!key.startsWith("_") && !key.equals("q") && !key.equals("probeId")) {
                    Path<?> path = root.get(key);
                    if (path.getJavaType().equals(String.class)) {
                        predicate[0] = cb.and(predicate[0], cb.like(cb.lower(path.as(String.class)), "%" + value.toLowerCase() + "%"));
                    }
                }
            });

            return predicate[0];
        };

        Page<Probe> result = probeRepository.findAll(spec, pageable);
        logger.info("Total elements: {}, Content size: {}", result.getTotalElements(), result.getContent().size());
        long total = result.getTotalElements();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(total));

        return new ResponseEntity<>(result.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Probe> getById(@PathVariable UUID id) {
        return probeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Probe createProbe(@RequestBody Probe probe) {
        return probeRepository.save(probe);
    }

    @PutMapping("/{id}")
    public Probe updateProbe(@PathVariable UUID id, @RequestBody Probe probe) {
        probe.setId(id);
        return probeRepository.save(probe);
    }

    @DeleteMapping("/{id}")
    public void deleteProbe(@PathVariable UUID id) {
        probeRepository.deleteById(id);
    }
}