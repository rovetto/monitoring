package ch.pinet.appmonitoring.managementapi.controller;

import ch.pinet.appmonitoring.managementapi.entities.Probe;
import ch.pinet.appmonitoring.managementapi.service.ProbeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PrometheusConfigControllerTest {

    @Mock
    private ProbeService probeService;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PrometheusConfigController prometheusConfigController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateAndSendConfig() {
        List<Probe> probes = List.of(
//                new Probe()
//                        .setId(UUID.fromString("486cad2e-6bf3-4cbc-816a-8e0bf21bc84b"))
//                        .setPort(9090)
//                        .setIpv4("127.0.0.1")
//                        .setDnsName("localhost")
//                        .setBezeichnung("prometheus"),
//                new Probe()
//                        .setId(UUID.fromString("075397d1-ef64-4fee-be78-3352a488bafb"))
//                        .setPort(8093)
//                        .setIpv4("127.0.0.1")
//                        .setDnsName("localhost")
//                        .setBezeichnung("prometheus-api")
        );
        when(probeService.getAllProbes()).thenReturn(probes);
        when(templateEngine.process(eq("prometheus-config"), any(Context.class))).thenReturn(
                """
                global:
                  scrape_interval: 15s
                  evaluation_interval: 15s
    
                scrape_configs:
    
                  - job_name: "prometheus"
                    static_configs:
                      - targets: ["127.0.0.1:9090"]
    
                  - job_name: "prometheus-api"
                    static_configs:
                      - targets: ["127.0.0.1:8093"]
                """
        );
        when(restTemplate.postForObject(eq("http://localhost:8093/config"), any(), eq(String.class))).thenReturn("Config accepted");

        String result = prometheusConfigController.generateAndSendConfig();

        verify(probeService, times(1)).getAllProbes();
        verify(templateEngine, times(1)).process(eq("prometheus-config"), any(Context.class));
        verify(restTemplate, times(1)).postForObject(eq("http://localhost:8093/config"), any(), eq(String.class));

        assertTrue(result.contains("job_name: \"prometheus\""));
        assertTrue(result.contains("targets: [\"127.0.0.1:9090\"]"));
        assertTrue(result.contains("job_name: \"prometheus-api\""));
        assertTrue(result.contains("targets: [\"127.0.0.1:8093\"]"));
    }
}
