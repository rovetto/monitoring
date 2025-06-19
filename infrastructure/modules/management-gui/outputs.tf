# modules/management-gui/outputs.tf

output "service_name" {
  description = "Name of the Kubernetes service"
  value       = kubernetes_service.management_gui.metadata[0].name
}

output "service_port" {
  description = "Port of the Kubernetes service"
  value       = kubernetes_service.management_gui.spec[0].port[0].port
}
