variable "environment" {
  description = "Die Zielumgebung (z. B. local)"
  type        = string
}

variable "namespace" {
  description = "Kubernetes Namespace für Postgres"
  type        = string
}

variable "postgres_password" {
  description = "Passwort für Postgres"
  type        = string
  sensitive   = true
}

resource "kubernetes_deployment" "postgres" {
  metadata {
    name = "postgres-${var.environment}"
    namespace = var.namespace
    labels = {
      app = "postgres"
      env = var.environment
    }
  }
  spec {
    replicas = 1
    selector {
      match_labels = {
        app = "postgres"
        env = var.environment
      }
    }
    template {
      metadata {
        labels = {
          app = "postgres"
          env = var.environment
        }
      }
      spec {
        container {
          image = "postgres:15"
          name  = "postgres"
          env {
            name  = "POSTGRES_PASSWORD"
            value = var.postgres_password
          }
          port {
            container_port = 5432
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "postgres" {
  metadata {
    name = "postgres-${var.environment}"
    namespace = var.namespace
  }
  spec {
    selector = {
      app = "postgres"
      env = var.environment
    }
    port {
      port        = 5432
      target_port = 5432
    }
    type = "ClusterIP"
  }
}
