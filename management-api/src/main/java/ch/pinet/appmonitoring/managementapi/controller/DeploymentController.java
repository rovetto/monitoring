package ch.pinet.appmonitoring.managementapi.controller;

import ch.pinet.appmonitoring.managementapi.entities.Probe;
import ch.pinet.appmonitoring.managementapi.service.ProbeService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DeploymentController {

    private final ProbeService probeService;
    private final SpringTemplateEngine templateEngine;
    private final RestTemplate restTemplate;

    @Value("${deployment.prometheus.config-url}")
    private String prometheusConfigUrl;

    @Value("${deployment.grafana.config-url}")
    private String grafanaConfigUrl;

    @Value("${deployment.otel-collector.config-url}")
    private String otelCollectorConfigUrl;

    @Autowired
    public DeploymentController(ProbeService probeService, SpringTemplateEngine templateEngine, RestTemplate restTemplate) {
        this.probeService = probeService;
        this.templateEngine = templateEngine;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/deploy/{component}")
    public ResponseEntity<String> deployComponent(
            @Parameter(
                    required = true,
                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                            allowableValues = {"prometheus", "grafana", "otel-collector"}
                    )
            )
            @PathVariable String component
    ) {
        try {
            String config = generateConfig(component);
            sendConfig(component, config);
            triggerDeployment(component);
            return ResponseEntity.ok("Deployment für " + component + " erfolgreich angestoßen. Config:\n" + config);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fehler beim Deploy von " + component + ": " + e.getMessage());
        }
    }

    private String generateConfig(String component) {
        List<Probe> probes = probeService.getAllProbes();
        Context context = new Context();
        context.setVariable("probes", probes);

        return switch (component.toLowerCase()) {
            case "prometheus" -> templateEngine.process("prometheus-config", context);
            case "grafana" -> templateEngine.process("grafana-config", context);
            case "otel-collector" -> templateEngine.process("otel-collector-config", context);
            default -> throw new IllegalArgumentException("Unbekannte Komponente: " + component);
        };
    }

    private void sendConfig(String component, String config) {
        String url = switch (component.toLowerCase()) {
            case "prometheus" -> prometheusConfigUrl;
            case "grafana" -> grafanaConfigUrl;
            case "otel-collector" -> otelCollectorConfigUrl;
            default -> throw new IllegalArgumentException("Unbekannte Komponente: " + component);
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> request = new HttpEntity<>(config, headers);
        restTemplate.postForObject(url, request, String.class);
    }

    private void triggerDeployment(String component) {
        System.out.println("Deployment für " + component + " wird angestoßen (noch nicht implementiert)");
    }
}
