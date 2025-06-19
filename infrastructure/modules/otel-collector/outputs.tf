output "collector_service_name" {
  description = "Name of the OTel Collector Kubernetes service"
  value       = kubernetes_service.otel_collector.metadata[0].name
}

output "api_service_name" {
  description = "Name of the OTel Collector API Kubernetes service"
  value       = kubernetes_service.otel_collector_api.metadata[0].name
}
