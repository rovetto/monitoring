resource "kubernetes_deployment" "management_gui" {
  metadata {
    name      = "management-gui-${var.environment}"
    namespace = var.namespace
    labels = {
      app = "management-gui"
      env = var.environment
    }
  }
  spec {
    replicas = var.replicas
    selector {
      match_labels = {
        app = "management-gui"
        env = var.environment
      }
    }
    template {
      metadata {
        labels = {
          app = "management-gui"
          env = var.environment
        }
      }
      spec {
        image_pull_secrets {
          name = "gitlab-regcred"
        }
        container {
          name  = "management-gui"
          image = "${var.image_registry}management-gui:${var.environment}"
          env {
            name  = "ENVIRONMENT"
            value = var.environment
          }
          env {
            name  = "API_URL"
            value = var.api_url
          }
          port {
            container_port = var.container_port
          }
          resources {
            limits = {
              cpu    = var.cpu_limit
              memory = var.memory_limit
            }
            requests = {
              cpu    = var.cpu_request
              memory = var.memory_request
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "management_gui" {
  metadata {
    name      = "management-gui-${var.environment}"
    namespace = var.namespace
  }
  spec {
    selector = {
      app = "management-gui"
      env = var.environment
    }
    port {
      protocol    = "TCP"
      port        = var.service_port
      target_port = var.container_port
    }
    type = "ClusterIP"
  }
}
