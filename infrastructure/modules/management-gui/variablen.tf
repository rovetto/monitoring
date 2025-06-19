variable "namespace" {
  description = "Kubernetes namespace to deploy into"
  type        = string
}

variable "environment" {
  description = "Environment name (e.g., dev, prod)"
  type        = string
}

variable "api_url" {
  description = "Rest API"
  type        = string
}

variable "image_registry" {
  description = "Container registry URL (e.g., docker.io/username)"
  type        = string
  default     = ""
}

variable "image_tag" {
  description = "Image tag to deploy"
  type        = string
  default     = "local"
}

variable "replicas" {
  description = "Number of replicas for the deployment"
  type        = number
  default     = 1
}

variable "container_port" {
  description = "Port exposed by the container"
  type        = number
  default     = 8091
}

variable "service_port" {
  description = "Port exposed by the service"
  type        = number
  default     = 8091
}

variable "cpu_limit" {
  description = "CPU limit for the container"
  type        = string
  default     = "250m"
}

variable "memory_limit" {
  description = "Memory limit for the container"
  type        = string
  default     = "256Mi"
}

variable "cpu_request" {
  description = "CPU request for the container"
  type        = string
  default     = "100m"
}

variable "memory_request" {
  description = "Memory request for the container"
  type        = string
  default     = "128Mi"
}
