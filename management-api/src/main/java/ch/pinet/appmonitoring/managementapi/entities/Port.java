package ch.pinet.appmonitoring.managementapi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "port")
public class Port {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(columnDefinition = "uuid DEFAULT gen_random_uuid()", updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "port")
    private Integer port;

    @NotNull
    @Column(name = "ipv4")
    private String ipv4;

    @Size(max = 255)
    @Column(name = "dns_name")
    private String dnsName;

    @Size(max = 255)
    @Column(name = "bezeichnung")
    private String bezeichnung;

}
