#!/bin/bash
# Push jobs into Redis queue to trigger KEDA autoscaling

REDIS_POD=$(kubectl get pod -n keda-demo -l app=redis -o jsonpath='{.items[0].metadata.name}')
QUEUE_NAME="${1:-job-queue}"
JOB_COUNT="${2:-50}"

echo "=== KEDA Autoscaling Load Test ==="
echo "Redis pod : $REDIS_POD"
echo "Queue     : $QUEUE_NAME"
echo "Jobs      : $JOB_COUNT"
echo "=================================="

for i in $(seq 1 "$JOB_COUNT"); do
    kubectl exec -n keda-demo "$REDIS_POD" -- redis-cli RPUSH "$QUEUE_NAME" "job-$i-$(date +%s)"
    echo "Pushed job $i/$JOB_COUNT"
done

echo ""
echo "Done! Queue length:"
kubectl exec -n keda-demo "$REDIS_POD" -- redis-cli LLEN "$QUEUE_NAME"
echo ""
echo "Watch pods scale up:"
echo "  kubectl get pods -n keda-demo -w"
