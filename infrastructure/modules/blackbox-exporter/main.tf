variable "environment" {
  description = "The environment to deploy to"
  type        = string
}

variable "namespace" {
  description = "The Kubernetes namespace"
  type        = string
}

resource "kubernetes_deployment" "blackbox_exporter" {
  metadata {
    name      = "blackbox-exporter-${var.environment}"
    namespace = var.namespace
    labels = {
      app = "blackbox-exporter"
      env = var.environment
    }
  }
  spec {
    replicas = 1
    selector {
      match_labels = {
        app = "blackbox-exporter"
        env = var.environment
      }
    }
    template {
      metadata {
        labels = {
          app = "blackbox-exporter"
          env = var.environment
        }
      }
      spec {
        container {
          name  = "blackbox-exporter"
          image = "prom/blackbox-exporter:latest"
          port {
            container_port = 9115
          }
          args = [
            "--config.file=/etc/blackbox/blackbox.yml"
          ]
          volume_mount {
            mount_path = "/etc/blackbox"
            name       = "blackbox-config"
          }
        }
        volume {
          name = "blackbox-config"
          config_map {
            name = kubernetes_config_map.blackbox_config.metadata[0].name
          }
        }
      }
    }
  }
}

resource "kubernetes_config_map" "blackbox_config" {
  metadata {
    name      = "blackbox-config-${var.environment}"
    namespace = var.namespace
  }
  data = {
    "blackbox.yml" = <<EOF
modules:
  http_2xx:
    prober: http
    timeout: 5s
    http:
      valid_status_codes: [200]
      method: GET
  https_ssl:
    prober: http
    timeout: 5s
    http:
      valid_status_codes: [200]
      method: GET
      tls_config:
        insecure_skip_verify: false
EOF
  }
}

resource "kubernetes_service" "blackbox_exporter" {
  metadata {
    name      = "blackbox-exporter-${var.environment}"
    namespace = var.namespace
  }
  spec {
    selector = {
      app = "blackbox-exporter"
      env = var.environment
    }
    port {
      protocol    = "TCP"
      port        = 9115
      target_port = 9115
    }
    type = "ClusterIP"
  }
}
