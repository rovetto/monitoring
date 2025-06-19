package ch.pinet.appmonitoring.managementapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "probes")
public class Probe {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(columnDefinition = "uuid DEFAULT gen_random_uuid()", updatable = false, nullable = false)
    private UUID id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "environment")
    private Environment environment;

    @Column(name = "maintenance")
    private boolean maintenance;

    private int port;

    private String rule;

    private String url;

    private String remark;

    @Column(name = "check_http")
    private boolean checkHttp;

    @Column(name = "check_certificate")
    private boolean checkCertificate;

    @Column(name = "check_rule")
    private boolean checkRule;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id")
    private Service service;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "server_id")
    private Server server;

    @Transient
    public UUID getServiceId() {
        return service != null ? service.getId() : null;
    }

    @Transient
    public UUID getServerId() {
        return server != null ? server.getId() : null;
    }

}
