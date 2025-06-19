package ch.pinet.appmonitoring.managementapi.controller;

import ch.pinet.appmonitoring.managementapi.entities.Probe;
import ch.pinet.appmonitoring.managementapi.service.ProbeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PrometheusConfigController {

    private final ProbeService probeService;
    private final SpringTemplateEngine templateEngine;
    private final RestTemplate restTemplate;

    @Autowired
    public PrometheusConfigController(ProbeService probeService, SpringTemplateEngine templateEngine, RestTemplate restTemplate) {
        this.probeService = probeService;
        this.templateEngine = templateEngine;
        this.restTemplate = restTemplate;
    }

    public String generateAndSendConfig() {
        List<Probe> probes = probeService.getAllProbes();
        Context context = new Context();
        context.setVariable("probes", probes);
        String prometheusConfig = templateEngine.process("prometheus-config", context);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(prometheusConfig, headers);

        String url = "http://localhost:8093/config";
        String response = restTemplate.postForObject(url, request, String.class);

        System.out.println("Prometheus Config gesendet. Antwort: " + response);
        return prometheusConfig;
    }

    @GetMapping("/generate-prometheus-config")
    @ResponseBody
    public String triggerConfigGeneration() {
        return generateAndSendConfig();
    }

}
