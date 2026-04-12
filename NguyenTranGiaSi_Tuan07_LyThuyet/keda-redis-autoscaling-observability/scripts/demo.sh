#!/bin/bash
# Full demo flow: setup -> load -> observe -> scale-to-zero

set -e

echo "============================================"
echo "  KEDA Redis Autoscaling Demo"
echo "============================================"
echo ""

echo "[1/4] Current state: 0 worker pods (scale-to-zero)"
kubectl get pods -n keda-demo -l app=worker-service
echo ""
read -p "Press Enter to push 30 jobs into Redis..."

echo ""
echo "[2/4] Pushing 30 jobs..."
REDIS_POD=$(kubectl get pod -n keda-demo -l app=redis -o jsonpath='{.items[0].metadata.name}')
for i in $(seq 1 30); do
    kubectl exec -n keda-demo "$REDIS_POD" -- redis-cli RPUSH job-queue "job-$i" > /dev/null
done
echo "Queue length: $(kubectl exec -n keda-demo "$REDIS_POD" -- redis-cli LLEN job-queue)"
echo ""

echo "[3/4] Watch KEDA scale up pods (wait ~15s)..."
echo "Checking every 5 seconds for 60 seconds..."
for i in $(seq 1 12); do
    QUEUE_LEN=$(kubectl exec -n keda-demo "$REDIS_POD" -- redis-cli LLEN job-queue 2>/dev/null || echo "0")
    POD_INFO=$(kubectl get pods -n keda-demo -l app=worker-service --no-headers 2>/dev/null || echo "No pods")
    POD_COUNT=$(echo "$POD_INFO" | grep -c "Running" || echo "0")
    echo "  [$(date +%H:%M:%S)] Queue: $QUEUE_LEN | Running: $POD_COUNT"
    sleep 5
done

echo ""
echo "[4/4] Jobs processed. Watch scale-to-zero (cooldown: 30s)..."
echo "Checking every 10 seconds..."
for i in $(seq 1 6); do
    TOTAL=$(kubectl get pods -n keda-demo -l app=worker-service --no-headers 2>/dev/null | wc -l | tr -d ' ')
    echo "  [$(date +%H:%M:%S)] Worker pods: $TOTAL"
    sleep 10
done

echo ""
echo "============================================"
echo "  Demo Complete!"
echo "  Open Grafana to see the scaling graph"
echo "============================================"
