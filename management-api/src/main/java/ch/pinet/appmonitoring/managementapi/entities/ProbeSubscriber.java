package ch.pinet.appmonitoring.managementapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "probe_subscribers")
public class ProbeSubscriber {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(columnDefinition = "uuid DEFAULT gen_random_uuid()", updatable = false, nullable = false)
    private UUID id;

    private boolean owner;

    private boolean support;

    private boolean customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "probe_id", nullable = false)
    private Probe probe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id", nullable = false)
    private Subscriber subscriber;

    @Transient
    public UUID getProbeId() {
        return probe != null ? probe.getId() : null;
    }

    @Transient
    public UUID getSubscriberId() {
        return subscriber != null ? subscriber.getId() : null;
    }

}
