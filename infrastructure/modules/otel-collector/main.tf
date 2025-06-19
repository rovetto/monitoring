resource "kubernetes_config_map" "otel_collector_config" {
  metadata {
    name      = "otel-collector-config-${var.environment}"
    namespace = var.namespace
  }
  data = {
    "otel-collector-config.yaml" = <<EOF
receivers:
  otlp:
    protocols:
      grpc:
        endpoint: 0.0.0.0:4317
      http:
        endpoint: 0.0.0.0:4318

processors:
  batch:

exporters:
  prometheus:
    endpoint: "0.0.0.0:8889"  # Metriken an Prometheus
  otlphttp:
    endpoint: "http://loki-${var.environment}.${var.namespace}.svc.cluster.local:3100/loki/api/v1/push"  # Logs an Loki über OTLP

extensions:
  health_check:
    endpoint: "0.0.0.0:13133"
  zpages:
    endpoint: "0.0.0.0:55679"

service:
  extensions: [health_check, zpages]
  pipelines:
    metrics:
      receivers: [otlp]
      processors: [batch]
      exporters: [prometheus]
    logs:
      receivers: [otlp]
      processors: [batch]
      exporters: [otlphttp]
EOF
  }
}

resource "kubernetes_deployment" "otel_collector" {
  metadata {
    name      = "otel-collector-${var.environment}"
    namespace = var.namespace
    labels = {
      app = "otel-collector"
      env = var.environment
    }
  }
  spec {
    replicas = var.replicas
    selector {
      match_labels = {
        app = "otel-collector"
        env = var.environment
      }
    }
    template {
      metadata {
        labels = {
          app = "otel-collector"
          env = var.environment
        }
      }
      spec {
        image_pull_secrets {
          name = "gitlab-regcred"
        }
        container {
          name  = "otel-collector"
          image = "otel/opentelemetry-collector-contrib:latest"
          args  = ["--config=/etc/otel-config/otel-collector-config.yaml"]
          port {
            container_port = 4317   # OTLP gRPC
          }
          port {
            container_port = 4318   # OTLP HTTP
          }
          port {
            container_port = 8889   # Prometheus Metrics
          }
          port {
            container_port = 13133  # Health Check und Reload
          }
          port {
            container_port = 55679  # ZPages (optional)
          }
          volume_mount {
            name       = "otel-config"
            mount_path = "/etc/otel-config"
          }
          # resources {
          #   limits = {
          #     cpu    = var.collector_cpu_limit
          #     memory = var.collector_memory_limit
          #   }
          #   requests = {
          #     cpu    = var.collector_cpu_request
          #     memory = var.collector_memory_request
          #   }
          #}
        }
        container {
          name  = "otel-collector-api"
          image = "${var.image_registry}otel-collector-api:${var.environment}"
          port {
            container_port = 8095  # REST-API
          }
          volume_mount {
            name       = "otel-config"
            mount_path = "/etc/otel-config"
          }
          # resources {
          #   limits = {
          #     cpu    = var.api_cpu_limit
          #     memory = var.api_memory_limit
          #   }
          #   requests = {
          #     cpu    = var.api_cpu_request
          #     memory = var.api_memory_request
          #   }
          # }
        }
        volume {
          name = "otel-config"
          config_map {
            name = kubernetes_config_map.otel_collector_config.metadata[0].name
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "otel_collector" {
  metadata {
    name      = "otel-collector-${var.environment}"
    namespace = var.namespace
  }
  spec {
    selector = {
      app = "otel-collector"
      env = var.environment
    }
    port {
      name        = "otlp-grpc"
      protocol    = "TCP"
      port        = 4317
      target_port = 4317
    }
    port {
      name        = "otlp-http"
      protocol    = "TCP"
      port        = 4318
      target_port = 4318
    }
    port {
      name        = "prometheus"
      protocol    = "TCP"
      port        = 8889
      target_port = 8889
    }
    port {
      name        = "healthcheck"
      protocol    = "TCP"
      port        = 13133
      target_port = 13133
    }
    port {
      name        = "zpages"
      protocol    = "TCP"
      port        = 55679
      target_port = 55679
    }
    type = "ClusterIP"
  }
}

resource "kubernetes_service" "otel_collector_api" {
  metadata {
    name      = "otel-collector-api-${var.environment}"
    namespace = var.namespace
  }
  spec {
    selector = {
      app = "otel-collector"
      env = var.environment
    }
    port {
      name        = "api"
      protocol    = "TCP"
      port        = 8095
      target_port = 8095
    }
    type = "ClusterIP"
  }
}
