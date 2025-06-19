package ch.pinet.appmonitoring.managementapi.repository;

import ch.pinet.appmonitoring.managementapi.entities.Probe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ProbeRepository extends JpaRepository<Probe, UUID>, JpaSpecificationExecutor<Probe> {
}
