output "service_name" {
  description = "Name of the Kubernetes service"
  value       = kubernetes_service.management_api.metadata[0].name
}

output "service_port" {
  description = "Port of the Kubernetes service"
  value       = kubernetes_service.management_api.spec[0].port[0].port
}
