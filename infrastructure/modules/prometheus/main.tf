variable "namespace" {
  description = "Kubernetes Namespace"
  type        = string
  default     = "monitoring"
}

variable "environment" {
  description = "Die Zielumgebung (local, dev, test, prod)"
  type        = string
  default     = "local"
}

variable "image_registry" {
  description = "Registry"
  type        = string
}

resource "kubernetes_config_map" "prometheus_config" {
  metadata {
    name      = "prometheus-config-${var.environment}"
    namespace = var.namespace
  }
  data = {
    "prometheus.yml" = templatefile("${path.module}/prometheus.yml.tmpl", {
      environment = var.environment
      namespace   = var.namespace
    })
  }
}

resource "kubernetes_deployment" "prometheus" {
  metadata {
    name      = "prometheus-${var.environment}"
    namespace = var.namespace
    labels = {
      app = "prometheus"
      env = var.environment
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "prometheus"
        env = var.environment
      }
    }

    template {
      metadata {
        labels = {
          app = "prometheus"
          env = var.environment
        }
      }

      spec {

        image_pull_secrets {
          name = "gitlab-regcred"
        }

        container {
          name  = "prometheus"
          image = "prom/prometheus:latest"

          port {
            container_port = 9090
          }

          volume_mount {
            mount_path = "/etc/prometheus/generated-configs"
            name       = "generated-configs"
          }

          args = [
            "--config.file=/etc/prometheus/generated-configs/prometheus.yml",
            "--storage.tsdb.path=/prometheus",
            "--web.enable-lifecycle"
          ]
        }

        container {
          name  = "prometheus-api"
          image = "${var.image_registry}prometheus-api:${var.environment}"

          port {
            container_port = 8093
          }

          env {
            name  = "SPRING_PROFILES_ACTIVE"
            value = var.environment
          }

          volume_mount {
            mount_path = "/etc/prometheus/generated-configs"
            name       = "generated-configs"
          }
        }

        init_container {
          name    = "init-config"
          image   = "busybox:latest"
          command = ["sh", "-c", "cp /etc/prometheus/initial-config/prometheus.yml /etc/prometheus/generated-configs/prometheus.yml"]

          volume_mount {
            mount_path = "/etc/prometheus/initial-config"
            name       = "config"
          }

          volume_mount {
            mount_path = "/etc/prometheus/generated-configs"
            name       = "generated-configs"
          }
        }

        volume {
          name = "config"
          config_map {
            name = kubernetes_config_map.prometheus_config.metadata[0].name
          }
        }

        volume {
          name = "generated-configs"
          empty_dir {}
        }
      }
    }
  }
}

resource "kubernetes_service" "prometheus" {
  metadata {
    name      = "prometheus-${var.environment}"
    namespace = var.namespace
  }

  spec {
    selector = {
      app = "prometheus"
      env = var.environment
    }

    port {
      protocol    = "TCP"
      port        = 9090
      target_port = 9090
    }

    type = "ClusterIP"
  }
}

resource "kubernetes_service" "prometheus_api" {
  metadata {
    name      = "prometheus-api-${var.environment}"
    namespace = var.namespace
  }

  spec {
    selector = {
      app = "prometheus"
      env = var.environment
    }

    port {
      port        = 8093
      target_port = 8093
    }

    type = "ClusterIP"
  }
}
