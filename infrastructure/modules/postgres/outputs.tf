output "service_name" {
  description = "Name des Postgres-Services"
  value       = kubernetes_service.postgres.metadata[0].name
}
