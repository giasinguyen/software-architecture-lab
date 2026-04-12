# KEDA Redis Autoscaling with Observability

Event-driven autoscaling demo: Spring Boot worker consumes Redis queue, KEDA scales pods from 0 to N based on queue length, Prometheus + Grafana visualize everything.

## Architecture

```
                    ┌─────────────┐
                    │  Load Test  │
                    │   Script    │
                    └──────┬──────┘
                           │ RPUSH jobs
                           ▼
                    ┌─────────────┐
                    │    Redis    │
                    │  (Queue)    │
                    └──────┬──────┘
                           │ monitors queue length
                           ▼
                    ┌─────────────┐
                    │    KEDA     │──── scales ────┐
                    │ ScaledObject│                 │
                    └─────────────┘                 ▼
                                          ┌──────────────────┐
                                          │  Worker Pods     │
                                          │  (0 → N → 0)    │
                                          │  Spring Boot     │
                                          └────────┬─────────┘
                                                   │ /actuator/prometheus
                                                   ▼
                                          ┌─────────────┐
                                          │ Prometheus   │
                                          └──────┬──────┘
                                                 │
                                                 ▼
                                          ┌─────────────┐
                                          │  Grafana     │
                                          │  Dashboard   │
                                          └─────────────┘
```

**Flow:**
1. Script pushes jobs into Redis list (`job-queue`)
2. KEDA polls Redis every 5s, checks queue length
3. If queue length > 0 → KEDA scales worker pods up (1 pod per 5 jobs)
4. Workers consume jobs with 2s processing delay
5. Queue empty for 30s → KEDA scales pods to zero
6. Prometheus scrapes metrics, Grafana displays dashboards

## Why KEDA over HPA?

| Feature | HPA | KEDA |
|---------|-----|------|
| Scale to zero | No (min=1) | Yes |
| Event sources | CPU/Memory only | 60+ (Redis, Kafka, RabbitMQ...) |
| External metrics | Needs adapter | Built-in |
| Queue-based scaling | Not supported | Native |

HPA watches CPU/Memory. KEDA watches the actual workload (queue length), making scaling decisions based on real demand.

## Project Structure

```
├── worker-service/          # Spring Boot worker
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
├── k8s/                     # Kubernetes manifests
│   ├── namespace.yaml
│   ├── redis.yaml
│   ├── worker-deployment.yaml
│   ├── keda-scaledobject.yaml
│   └── service-monitor.yaml
├── grafana/
│   └── dashboard.json       # Pre-built Grafana dashboard
└── scripts/
    ├── load-test.sh          # Push jobs to queue
    ├── monitor.sh            # Real-time CLI monitor
    └── demo.sh               # Full demo flow
```

## Prerequisites

- Docker Desktop
- minikube
- kubectl
- Helm

## Setup Guide (< 20 minutes)

### Step 1: Start Minikube

```bash
minikube start --cpus=4 --memory=6144 --driver=docker
```

### Step 2: Build Worker Image

Build inside minikube's Docker daemon so the image is available to K8s:

```bash
eval $(minikube docker-env)

cd worker-service
docker build -t worker-service:latest .
cd ..
```

**Windows PowerShell:**
```powershell
minikube docker-env --shell powershell | Invoke-Expression

cd worker-service
docker build -t worker-service:latest .
cd ..
```

### Step 3: Install KEDA

```bash
helm repo add kedacore https://kedacore.github.io/charts
helm repo update

helm install keda kedacore/keda \
  --namespace keda \
  --create-namespace \
  --wait
```

### Step 4: Install Prometheus + Grafana

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

helm install prometheus prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  --create-namespace \
  --set prometheus.prometheusSpec.podMonitorSelectorNilUsesHelmValues=false \
  --set prometheus.prometheusSpec.serviceMonitorSelectorNilUsesHelmValues=false \
  --wait
```

### Step 5: Deploy Application

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/redis.yaml
kubectl apply -f k8s/worker-deployment.yaml
kubectl apply -f k8s/keda-scaledobject.yaml
kubectl apply -f k8s/service-monitor.yaml
```

### Step 6: Verify Deployment

```bash
# Redis should be running, worker-service should have 0 pods
kubectl get pods -n keda-demo

# KEDA ScaledObject should be active
kubectl get scaledobject -n keda-demo
```

Expected output:
```
NAME              READY   STATUS    RESTARTS   AGE
redis-xxx-xxx     1/1     Running   0          30s

NAME                  SCALETARGETKIND      SCALETARGETNAME   MIN   MAX   TRIGGERS   READY   ACTIVE
worker-scaledobject   apps/v1.Deployment   worker-service    0     10    redis      True    False
```

**Zero worker pods** — this is KEDA's scale-to-zero working.

## Load Testing

### Push Jobs

```bash
chmod +x scripts/*.sh

# Push 50 jobs (default)
./scripts/load-test.sh

# Push custom amount
./scripts/load-test.sh job-queue 100
```

### Watch Scaling

Terminal 1 — watch pods:
```bash
kubectl get pods -n keda-demo -w
```

Terminal 2 — monitor script:
```bash
./scripts/monitor.sh
```

You'll see:
1. **0 pods** → jobs pushed → **pods spinning up** (15-30s)
2. Workers processing → **queue draining**
3. Queue empty → cooldown → **pods terminate** (30s)

## Monitoring with Grafana

### Access Grafana

```bash
kubectl port-forward svc/prometheus-grafana -n monitoring 3000:80
```

Open http://localhost:3000

- **Username:** `admin`
- **Password:** `prom-operator`

### Import Dashboard

1. Go to **Dashboards** → **New** → **Import**
2. Upload `grafana/dashboard.json`
3. Select **Prometheus** as data source
4. Click **Import**

### Dashboard Panels

| Panel | Shows |
|-------|-------|
| Worker Pod Count | Number of active worker pods over time |
| CPU Usage per Pod | CPU consumption per worker |
| Memory Usage per Pod | Memory per worker |
| KEDA Scaling Metrics | Current active worker count (stat) |
| Pod Restarts | Container restart count |
| Desired vs Available | KEDA's desired replicas vs actual |

### Other Useful Dashboards

From Grafana marketplace (import by ID):

| Dashboard | ID | Purpose |
|-----------|----|---------|
| Kubernetes Pods | 15760 | Detailed pod metrics |
| Node Exporter | 1860 | Node-level metrics |

## 5-Minute Demo Script

### Setup (before presentation)
```bash
minikube start --cpus=4 --memory=6144 --driver=docker
# ... complete Steps 2-5 above
# Verify: kubectl get pods -n keda-demo (only Redis running)
# Start Grafana port-forward in background
```

### Live Demo

**Minute 1 — Show Zero State**
```bash
kubectl get pods -n keda-demo
# → Only redis pod, zero workers
kubectl get scaledobject -n keda-demo
# → ACTIVE: False
```
> "The system has zero worker pods. No resources wasted."

**Minute 2 — Generate Load**
```bash
./scripts/load-test.sh job-queue 30
```
> "30 jobs pushed to Redis. KEDA polls every 5 seconds..."

**Minute 3 — Show Scale-Up**
```bash
kubectl get pods -n keda-demo -w
# Pods appearing: worker-service-xxx ContainerCreating → Running
```
> "KEDA detected the queue and scaled workers automatically."

**Minute 4 — Show Grafana**
> Switch to Grafana dashboard. Show pod count rising, CPU usage, desired vs available.

**Minute 5 — Scale to Zero**
```bash
kubectl get pods -n keda-demo
# Workers terminating as queue drains → eventually 0 workers
```
> "Queue is empty. After 30s cooldown, pods scale back to zero. This is the key advantage of KEDA over HPA."

## Key KEDA Configuration Explained

```yaml
# keda-scaledobject.yaml
spec:
  pollingInterval: 5      # Check queue every 5 seconds
  cooldownPeriod: 30      # Wait 30s after queue empty to scale down
  minReplicaCount: 0      # Scale to zero (!!!)  
  maxReplicaCount: 10     # Max 10 workers
  triggers:
    - type: redis
      metadata:
        listName: job-queue
        listLength: "5"   # 1 pod per 5 jobs in queue
```

Scaling formula: `pods = ceil(queueLength / listLength)`
- 5 jobs → 1 pod
- 15 jobs → 3 pods
- 50 jobs → 10 pods (capped at max)

## Troubleshooting

### Pods not scaling up
```bash
# Check KEDA operator logs
kubectl logs -n keda -l app=keda-operator --tail=50

# Check ScaledObject status
kubectl describe scaledobject worker-scaledobject -n keda-demo
```

### Worker can't connect to Redis
```bash
# Test Redis connectivity
kubectl exec -n keda-demo deploy/redis -- redis-cli PING
# Should return: PONG
```

### Image pull error
```bash
# Make sure you built the image in minikube's Docker
eval $(minikube docker-env)
docker images | grep worker-service
```

### Check worker logs
```bash
kubectl logs -n keda-demo -l app=worker-service --tail=20
```

## Cleanup

```bash
kubectl delete -f k8s/
helm uninstall keda -n keda
helm uninstall prometheus -n monitoring
minikube stop
```
