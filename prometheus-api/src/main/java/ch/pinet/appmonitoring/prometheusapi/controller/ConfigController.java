package ch.pinet.appmonitoring.prometheusapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/config")
@Tag(name = "Prometheus API", description = "Prometheus Konfiguration")
public class ConfigController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);
    private static final String CONFIG_DIR = "/etc/prometheus/generated-configs";
    private static final String CONFIG_PATH = CONFIG_DIR + "/prometheus.yml";
    private static final String LOCAL_CONFIG_PATH = "./prometheus.yml";

    @Autowired
    private RestTemplate restTemplate;

    @Operation(summary = "Speichert eine neue Konfiguration")
    @PostMapping
    public ResponseEntity<String> saveConfig(@RequestBody String config) {
        try {
            Path configPath = determineConfigPath();
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, config);
            logger.info("✅ Konfiguration gespeichert unter: {}", configPath);

            restTemplate.postForObject("http://localhost:9090/-/reload", null, String.class);
            return ResponseEntity.ok("✅ Konfiguration gespeichert und Prometheus neu geladen: " + configPath);
        } catch (IOException e) {
            logger.error("❌ Fehler beim Speichern der Konfiguration: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Fehler beim Speichern: " + e.getMessage());
        }
    }

    @Operation(summary = "Liest die gespeicherte Konfiguration")
    @GetMapping
    public ResponseEntity<String> getConfig() {
        try {
            Path configPath = determineConfigPath();
            if (Files.exists(configPath)) {
                String config = Files.readString(configPath, StandardCharsets.UTF_8);
                logger.info("✅ Konfiguration geladen von: {}", configPath);
                return ResponseEntity.ok(config);
            } else {
                logger.warn("⚠️ Keine Konfiguration gefunden unter: {}", configPath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("⚠️ Keine Konfiguration gefunden unter: " + configPath);
            }
        } catch (IOException e) {
            logger.error("❌ Fehler beim Lesen der Konfiguration: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Fehler beim Lesen: " + e.getMessage());
        }
    }

    private Path determineConfigPath() {
        Path sidecarPath = Paths.get(CONFIG_PATH);
        if (Files.exists(Paths.get(CONFIG_DIR))) {
            return sidecarPath;
        } else {
            Path localPath = Paths.get(LOCAL_CONFIG_PATH);
            logger.info("Verzeichnis {} nicht gefunden, verwende lokalen Pfad: {}", CONFIG_DIR, localPath);
            return localPath;
        }
    }
}
