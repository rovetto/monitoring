variable "kube_config_path" {
  description = "Pfad zur Kubernetes-Konfigurationsdatei"
  type        = string
  sensitive   = true
  default     = "~/.kube/config"
}

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

variable "api_url" {
  description = "REST-Api für Node.js"
  type        = string
}

variable "postgres_password" {
  description = "Passwort für Postgres"
  type        = string
  sensitive   = true
}

variable "image_registry" {
  description = "Registry"
  type        = string
}

variable "gitlab_ci_token" {
  description = "gitlab_ci_token"
  type        = string
}
