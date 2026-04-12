#!/bin/bash
# Monitor queue length and pod count in real-time

QUEUE_NAME="${1:-job-queue}"

echo "=== Real-time Monitor ==="
echo "Press Ctrl+C to stop"
echo "========================="

while true; do
    REDIS_POD=$(kubectl get pod -n keda-demo -l app=redis -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)
    QUEUE_LEN=$(kubectl exec -n keda-demo "$REDIS_POD" -- redis-cli LLEN "$QUEUE_NAME" 2>/dev/null)
    POD_COUNT=$(kubectl get pods -n keda-demo -l app=worker-service --no-headers 2>/dev/null | grep -c Running)
    TOTAL_PODS=$(kubectl get pods -n keda-demo -l app=worker-service --no-headers 2>/dev/null | wc -l)

    echo "[$(date +%H:%M:%S)] Queue: ${QUEUE_LEN:-0} | Running pods: ${POD_COUNT:-0} | Total pods: ${TOTAL_PODS:-0}"
    sleep 3
done
