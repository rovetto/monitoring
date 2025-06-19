.PHONY: build docker terraform kubectl all

# 1. Maven Build
build:
	@echo "🔨 Starte Maven Build..."
	mvn clean package

# 2. Docker Images bauen
docker: build
	@echo "🐳 Baue Docker-Images..."
	docker build --no-cache -t grafana-api:local -f grafana-api/Dockerfile grafana-api/
	docker build --no-cache -t management-api:local -f management-api/Dockerfile management-api/
	docker build --no-cache -t otel-collector-api:local -f otel-collector-api/Dockerfile otel-collector-api/
	docker build --no-cache -t prometheus-api:local -f prometheus-api/Dockerfile prometheus-api/
	docker build --no-cache -t management-gui:local -f management-gui/Dockerfile management-gui/

# 3. Terraform Init & Apply
terraform:
	@echo "🌍 Wende Terraform an..."
	cd infrastructure && terraform init && terraform apply -var-file=environments/local.tfvars -state=terraform.local.tfstate -auto-approve

# 4. Kubernetes Pods prüfen
kubectl:
	@echo "🚀 Prüfe Pods..."
	kubectl get pods --all-namespaces

# 5. Alles zusammen starten
all: docker terraform kubectl
	@echo "✅ Build & Deployment abgeschlossen!"
