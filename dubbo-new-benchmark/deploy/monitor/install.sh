helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

helm install --set nodeExporter.enabled=false,alertmanager.enabled=false,prometheusOperator.admissionWebhooks.patch.image.registry="docker.io",prometheusOperator.admissionWebhooks.patch.image.repository=15841721425/kube-webhook-certgen kube-prometheus-stack --set kube-state-metrics.image.registry="docker.io",kube-state-metrics.image.repository=bitnami/kube-state-metrics,kube-state-metrics.image.tag=2.10.1 prometheus-community/kube-prometheus-stack -n monitoring