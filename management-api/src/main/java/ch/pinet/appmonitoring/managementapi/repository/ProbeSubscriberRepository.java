package ch.pinet.appmonitoring.managementapi.repository;

import ch.pinet.appmonitoring.managementapi.entities.ProbeSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProbeSubscriberRepository extends JpaRepository<ProbeSubscriber, UUID>, JpaSpecificationExecutor<ProbeSubscriber> {
}
