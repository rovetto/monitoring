package ch.pinet.appmonitoring.managementapi.controller;

import ch.pinet.appmonitoring.managementapi.entities.Subscriber;
import ch.pinet.appmonitoring.managementapi.repository.SubscriberRepository;
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
@RequestMapping("/subscribers")
@CrossOrigin(origins = "*", exposedHeaders = "X-Total-Count") // wichtig für react-admin
public class SubscriberController {

    private final SubscriberRepository subscriberRepository;

    public SubscriberController(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    @GetMapping
    public ResponseEntity<List<Subscriber>> getSubscribers(
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

        Specification<Subscriber> spec = (root, query, cb) -> {
            final Predicate[] predicate = {cb.conjunction()};

            // Freitextsuche für Autocomplete (über name, vorname, email, mobile)
            if (q != null && !q.isEmpty()) {
                String searchPattern = "%" + q.toLowerCase() + "%";
                Predicate namePredicate = cb.like(cb.lower(root.get("name")), searchPattern);
                Predicate vornamePredicate = cb.like(cb.lower(root.get("vorname")), searchPattern);
                Predicate emailPredicate = cb.like(cb.lower(root.get("email")), searchPattern);
                Predicate mobilePredicate = cb.like(cb.lower(root.get("mobile")), searchPattern);
                predicate[0] = cb.and(predicate[0], cb.or(namePredicate, vornamePredicate, emailPredicate, mobilePredicate));
            }

            // Filter für ReferenceField (subscriberId)
            if (allRequestParams.containsKey("subscriberId")) {
                try {
                    UUID subscriberId = UUID.fromString(allRequestParams.get("subscriberId"));
                    predicate[0] = cb.and(predicate[0], cb.equal(root.get("id"), subscriberId));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid UUID format for subscriberId: " + allRequestParams.get("subscriberId"), e);
                }
            }

            // Weitere flache Parameter (name, vorname, email, mobile)
            allRequestParams.forEach((key, value) -> {
                if (!key.startsWith("_") && !key.equals("q") && !key.equals("subscriberId")) {
                    Path<?> path = root.get(key);
                    if (path.getJavaType().equals(String.class)) {
                        predicate[0] = cb.and(predicate[0], cb.like(cb.lower(path.as(String.class)), "%" + value.toLowerCase() + "%"));
                    }
                }
            });

            return predicate[0];
        };

        Page<Subscriber> result = subscriberRepository.findAll(spec, pageable);
        logger.info("Total elements: {}, Content size: {}", result.getTotalElements(), result.getContent().size());
        long total = result.getTotalElements();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(total));

        return new ResponseEntity<>(result.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public Subscriber getSubscriber(@PathVariable UUID id) {
        return subscriberRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public Subscriber createSubscriber(@RequestBody Subscriber subscriber) {
        return subscriberRepository.save(subscriber);
    }

    @PutMapping("/{id}")
    public Subscriber updateSubscriber(@PathVariable UUID id, @RequestBody Subscriber subscriber) {
        subscriber.setId(id);
        return subscriberRepository.save(subscriber);
    }

    @DeleteMapping("/{id}")
    public void deleteSubscriber(@PathVariable UUID id) {
        subscriberRepository.deleteById(id);
    }
}
