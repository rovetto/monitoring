package ch.pinet.appmonitoring.grafanaapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/config")
@Tag(name = "Grafana API", description = "Grafana-Konfiguration")
public class ConfigController {

    @Value("${config.path}")
    private String configPath;

    @Operation(summary = "Speichert eine neue Konfiguration")
    @PostMapping
    public ResponseEntity<String> saveConfig(@RequestBody String config) {
        try {
            Files.writeString(Path.of(configPath), config);
            return ResponseEntity.ok("✅ Konfiguration gespeichert!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Fehler beim Speichern: " + e.getMessage());
        }
    }

    @Operation(summary = "Liest die gespeicherte Konfiguration")
    @GetMapping
    public ResponseEntity<String> getConfig() {
        try {
            if (Files.exists(Path.of(configPath))) {
                String config = Files.readString(Path.of(configPath), StandardCharsets.UTF_8);
                return ResponseEntity.ok(config);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("⚠️ Keine Konfiguration gefunden.");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Fehler beim Lesen: " + e.getMessage());
        }
    }

}
