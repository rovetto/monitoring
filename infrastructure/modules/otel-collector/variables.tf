variable "namespace" {
  description = "Kubernetes namespace to deploy into"
  type        = string
}

variable "environment" {
  description = "Environment name (e.g., dev, prod)"
  type        = string
}

variable "image_registry" {
  description = "Registry"
  type        = string
}

variable "replicas" {
  description = "Number of replicas for the deployment"
  type        = number
  default     = 1
}

variable "collector_cpu_limit" {
  description = "CPU limit for the otel-collector container"
  type        = string
  default     = "500m"
}

variable "collector_memory_limit" {
  description = "Memory limit for the otel-collector container"
  type        = string
  default     = "512Mi"
}

variable "collector_cpu_request" {
  description = "CPU request for the otel-collector container"
  type        = string
  default     = "250m"
}

variable "collector_memory_request" {
  description = "Memory request for the otel-collector container"
  type        = string
  default     = "256Mi"
}

variable "api_cpu_limit" {
  description = "CPU limit for the otel-collector-api container"
  type        = string
  default     = "250m"
}

variable "api_memory_limit" {
  description = "Memory limit for the otel-collector-api container"
  type        = string
  default     = "256Mi"
}

variable "api_cpu_request" {
  description = "CPU request for the otel-collector-api container"
  type        = string
  default     = "100m"
}

variable "api_memory_request" {
  description = "Memory request for the otel-collector-api container"
  type        = string
  default     = "128Mi"
}
