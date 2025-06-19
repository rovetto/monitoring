package ch.pinet.appmonitoring.managementapi.repository;

import ch.pinet.appmonitoring.managementapi.entities.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ServerRepository extends JpaRepository<Server, UUID>, JpaSpecificationExecutor<Server> {
}
