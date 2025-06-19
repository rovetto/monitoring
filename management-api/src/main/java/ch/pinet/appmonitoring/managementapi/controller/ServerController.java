package ch.pinet.appmonitoring.managementapi.controller;

import ch.pinet.appmonitoring.managementapi.entities.Server;
import ch.pinet.appmonitoring.managementapi.repository.ServerRepository;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/servers")
@CrossOrigin(origins = "*", exposedHeaders = "X-Total-Count") // wichtig für react-admin
public class ServerController {

    private final ServerRepository serverRepository;

    public ServerController(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @GetMapping
    public ResponseEntity<List<Server>> getServers(
            @RequestParam Optional<Integer> _page,
            @RequestParam Optional<Integer> _limit,
            @RequestParam Optional<String> _sort,
            @RequestParam Optional<String> _order,
            @RequestParam(required = false) String q, // Für Autocomplete
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

        Specification<Server> spec = (root, query, cb) -> {
            final Predicate[] predicate = {cb.conjunction()};

            // Freitextsuche für Autocomplete (über name, vorname, email, mobile)
            if (q != null && !q.isEmpty()) {
                String searchPattern = "%" + q.toLowerCase() + "%";
                Predicate namePredicate = cb.like(cb.lower(root.get("name")), searchPattern);
                predicate[0] = cb.and(predicate[0], cb.or(namePredicate));
            }

            // Filter für ReferenceField (serverId)
            if (allRequestParams.containsKey("serverId")) {
                try {
                    UUID serverId = UUID.fromString(allRequestParams.get("serverId"));
                    predicate[0] = cb.and(predicate[0], cb.equal(root.get("id"), serverId));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid UUID format for serverId: " + allRequestParams.get("serverId"), e);
                }
            }

            // Weitere flache Parameter (name, vorname, email, mobile)
            allRequestParams.forEach((key, value) -> {
                if (!key.startsWith("_") && !key.equals("q") && !key.equals("serverId")) {
                    Path<?> path = root.get(key);
                    if (path.getJavaType().equals(String.class)) {
                        predicate[0] = cb.and(predicate[0], cb.like(cb.lower(path.as(String.class)), "%" + value.toLowerCase() + "%"));
                    }
                }
            });

            return predicate[0];
        };

        Page<Server> result = serverRepository.findAll(spec, pageable);
        logger.info("Total elements: {}, Content size: {}", result.getTotalElements(), result.getContent().size());
        long total = result.getTotalElements();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(total));

        return new ResponseEntity<>(result.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public Server getServer(@PathVariable UUID id) {
        return serverRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public Server createServer(@RequestBody Server server) {
        return serverRepository.save(server);
    }

    @PutMapping("/{id}")
    public Server updateServer(@PathVariable UUID id, @RequestBody Server server) {
        server.setId(id);
        return serverRepository.save(server);
    }

    @DeleteMapping("/{id}")
    public void deleteServer(@PathVariable UUID id) {
        serverRepository.deleteById(id);
    }
}
