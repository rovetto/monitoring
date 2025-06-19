terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.0"
    }
  }
}

provider "kubernetes" {
  config_path = var.kube_config_path
  insecure = true
}

resource "kubernetes_namespace" "monitoring" {
  metadata {
    name = var.namespace
  }
  lifecycle {
    ignore_changes = [metadata]
  }
}

resource "kubernetes_secret" "gitlab_regcred" {
  type     = "kubernetes.io/dockerconfigjson"
  metadata {
    name      = "gitlab-regcred"
    namespace = var.namespace
  }
  data = {
    ".dockerconfigjson" = jsonencode({
      "auths" = {
        "https://gitlab-registry.pinet.ch" = {
          "username" = "gitlab-ci-token",
          "password" = var.gitlab_ci_token,
          "email"    = "not-used",
          "auth"     = base64encode("gitlab-ci-token:${var.gitlab_ci_token}")
        }
      }
    })
  }
}

module "prometheus" {
  source = "./modules/prometheus"
  environment       = var.environment
  namespace         = kubernetes_namespace.monitoring.metadata[0].name
  image_registry = var.image_registry
}

module "grafana" {
  source      = "./modules/grafana"
  environment = var.environment
  namespace   = kubernetes_namespace.monitoring.metadata[0].name
  image_registry = var.image_registry
}

module "management_api" {
  source = "./modules/management-api"
  namespace      = var.namespace
  environment    = var.environment
  image_tag      = var.environment
  container_port = 8092
  service_port   = 8092
  spring_profile = var.environment
  replicas       = 1
  cpu_limit      = "750m"
  memory_limit   = "768Mi"
  cpu_request    = "250m"
  memory_request = "384Mi"
  image_registry = var.image_registry
}

module "management_gui" {
  source         = "./modules/management-gui"
  namespace      = var.namespace
  environment    = var.environment
  image_tag      = var.environment
  container_port = 8091
  service_port   = 8091
  replicas       = 1
  cpu_limit      = "250m"
  memory_limit   = "256Mi"
  cpu_request    = "100m"
  memory_request = "128Mi"
  api_url        = var.api_url
  image_registry = var.image_registry
}

module "blackbox_exporter" {
  source      = "./modules/blackbox-exporter"
  environment = var.environment
  namespace   = var.namespace
}

module "postgres" {
  source            = "./modules/postgres"
  environment       = var.environment
  namespace         = kubernetes_namespace.monitoring.metadata[0].name
  postgres_password = var.postgres_password
}

# module "otel_collector" {
#   source             = "./modules/otel-collector"
#   namespace          = var.namespace
#   environment        = var.environment
#   image_registry = var.image_registry
# }
