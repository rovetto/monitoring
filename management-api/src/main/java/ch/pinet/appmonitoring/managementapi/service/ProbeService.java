package ch.pinet.appmonitoring.managementapi.service;

import ch.pinet.appmonitoring.managementapi.entities.Probe;
import ch.pinet.appmonitoring.managementapi.repository.ProbeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProbeService {

    private final ProbeRepository probeRepository;

    public ProbeService(ProbeRepository probeRepository) {
        this.probeRepository = probeRepository;
    }

    public List<Probe> getAllProbes() {
        return probeRepository.findAll();
    }
}
