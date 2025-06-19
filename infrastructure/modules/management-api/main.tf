resource "kubernetes_deployment" "management_api" {
  metadata {
    name      = "management-api-${var.environment}"
    namespace = var.namespace
    labels = {
      app = "management-api"
      env = var.environment
    }
  }
  spec {
    replicas = var.replicas
    selector {
      match_labels = {
        app = "management-api"
        env = var.environment
      }
    }
    template {
      metadata {
        labels = {
          app = "management-api"
          env = var.environment
        }
      }
      spec {

        image_pull_secrets {
          name = "gitlab-regcred"
        }

        container {
          name  = "management-api"
          image = "${var.image_registry}management-api:${var.environment}"
          port {
            container_port = var.container_port
          }
          env {
            name  = "SPRING_PROFILES_ACTIVE"
            value = var.spring_profile
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

resource "kubernetes_service" "management_api" {
  metadata {
    name      = "management-api-${var.environment}"
    namespace = var.namespace
  }
  spec {
    selector = {
      app = "management-api"
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
