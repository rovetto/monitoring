package ch.pinet.appmonitoring.managementapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "servers")
public class Server {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(columnDefinition = "uuid DEFAULT gen_random_uuid()", updatable = false, nullable = false)
    private UUID id;

    private String name;

}
