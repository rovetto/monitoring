variable "environment" {
  description = "Die Zielumgebung (local, dev, test, prod)"
  type        = string
}

variable "namespace" {
  description = "Kubernetes Namespace für Grafana"
  type        = string
}

variable "image_registry" {
  description = "Registry"
  type        = string
}

resource "kubernetes_config_map" "grafana_datasource" {
  metadata {
    name      = "grafana-datasource-${var.environment}"
    namespace = var.namespace
    labels = {
      grafana_datasource = "1"
      env                = var.environment
    }
  }
  data = {
    "prometheus-datasource.yaml" = templatefile("${path.module}/prometheus-datasource.yaml.tmpl", {
      environment = var.environment
      namespace   = var.namespace
    })
  }
}

resource "kubernetes_config_map" "grafana_dashboard_provisioning" {
  metadata {
    name      = "grafana-dashboard-provisioning-${var.environment}"
    namespace = var.namespace
    labels = {
      env = var.environment
    }
  }
  data = {
    "dashboards.yml" = <<EOT
apiVersion: 1
providers:
  - name: 'static-dashboards'
    orgId: 1
    folder: 'Static'
    type: file
    disableDeletion: false
    updateIntervalSeconds: 10
    options:
      path: /etc/grafana/provisioning/dashboards/static
  - name: 'generated-dashboards'
    orgId: 1
    folder: 'Generated'
    type: file
    disableDeletion: false
    updateIntervalSeconds: 10
    options:
      path: /var/lib/grafana/generated-dashboards
EOT
  }
}

resource "kubernetes_config_map" "grafana_static_dashboards" {
  metadata {
    name      = "grafana-static-dashboards-${var.environment}"
    namespace = var.namespace
    labels = {
      env = var.environment
    }
  }
  data = {
    "grafana-dashboard.json" = file("${path.module}/grafana-dashboard.json")
  }
}

resource "kubernetes_deployment" "grafana" {
  metadata {
    name      = "grafana-${var.environment}"
    namespace = var.namespace
    labels = { app = "grafana", env = var.environment }
  }
  spec {
    replicas = 1
    selector {
      match_labels = { app = "grafana", env = var.environment }
    }
    template {
      metadata {
        labels = {
          app = "grafana"
          env = var.environment
        }
      }
      spec {

        image_pull_secrets {
          name = "gitlab-regcred"
        }

        container {
          name  = "grafana"
          image = "grafana/grafana:latest"
          port { container_port = 3000 }
          liveness_probe {
            http_get {
              path = "/api/health"
              port = 3000
            }
            initial_delay_seconds = 30
            period_seconds        = 10
          }
          readiness_probe {
            http_get {
              path = "/api/health"
              port = 3000
            }
            initial_delay_seconds = 5
            period_seconds        = 10
          }
          volume_mount {
            name       = "dashboard-provisioning"
            mount_path = "/etc/grafana/provisioning/dashboards"
          }
          volume_mount {
            name       = "datasource-provisioning"
            mount_path = "/etc/grafana/provisioning/datasources"
          }
          volume_mount {
            name       = "static-dashboards"
            mount_path = "/etc/grafana/provisioning/dashboards/static"
            read_only  = true
          }
          volume_mount {
            name       = "generated-dashboards"
            mount_path = "/var/lib/grafana/generated-dashboards"
          }
          env {
            name  = "GF_AUTH_ANONYMOUS_ENABLED"
            value = "true"
          }
          env {
            name  = "GF_AUTH_ANONYMOUS_ORG_ROLE"
            value = "Admin"
          }
          env {
            name  = "GF_AUTH_DISABLE_LOGIN_FORM"
            value = "true"
          }
          env {
            name  = "GF_AUTH_BASIC_ENABLED"
            value = "false"
          }
        }
        container {
          name  = "grafana-api"
          image = "${var.image_registry}grafana-api:${var.environment}"
          port {
            container_port = 8094
          }
          volume_mount {
            name       = "generated-dashboards"
            mount_path = "/app/generated-dashboards"
          }
        }
        volume {
          name = "generated-dashboards"
          empty_dir {}
        }
        volume {
          name = "dashboard-provisioning"
          config_map {
            name = kubernetes_config_map.grafana_dashboard_provisioning.metadata[0].name
          }
        }
        volume {
          name = "datasource-provisioning"
          config_map {
            name = kubernetes_config_map.grafana_datasource.metadata[0].name
          }
        }
        volume {
          name = "static-dashboards"
          config_map {
            name = kubernetes_config_map.grafana_static_dashboards.metadata[0].name
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "grafana" {
  metadata {
    name      = "grafana-${var.environment}"
    namespace = var.namespace
  }
  spec {
    selector = {
      app = "grafana"
      env = var.environment
    }
    port {
      port        = 3000
      target_port = 3000
    }
    type = "ClusterIP"
  }
}

resource "kubernetes_service" "grafana_api" {
  metadata {
    name      = "grafana-api-${var.environment}"
    namespace = var.namespace
  }
  spec {
    selector = {
      app = "grafana"
      env = var.environment
    }
    port {
      port        = 8094
      target_port = 8094
    }
    type = "ClusterIP"
  }
}
