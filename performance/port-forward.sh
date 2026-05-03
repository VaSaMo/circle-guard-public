#!/bin/bash

NAMESPACE="taller2-dev"

echo "Stopping any existing port-forwards..."
pkill -f "port-forward"

echo "Starting port-forwards for CircleGuard microservices..."

# Port forward in background
kubectl port-forward svc/gateway-service 8087:8087 -n $NAMESPACE > /dev/null 2>&1 &
kubectl port-forward svc/auth-service 8180:8180 -n $NAMESPACE > /dev/null 2>&1 &
kubectl port-forward svc/identity-service 8083:8083 -n $NAMESPACE > /dev/null 2>&1 &
kubectl port-forward svc/form-service 8086:8086 -n $NAMESPACE > /dev/null 2>&1 &
kubectl port-forward svc/promotion-service 8088:8088 -n $NAMESPACE > /dev/null 2>&1 &
kubectl port-forward svc/notification-service 8082:8082 -n $NAMESPACE > /dev/null 2>&1 &

echo "Port-forwards active:"
echo "  - Gateway: http://localhost:8087"
echo "  - Auth:    http://localhost:8180"
echo "  - Identity: http://localhost:8083"
echo "  - Form:    http://localhost:8086"
echo "  - Promotion: http://localhost:8088"
echo "  - Notification: http://localhost:8082"
echo ""
echo "Press Ctrl+C to stop all port-forwards."

# Wait for Ctrl+C
trap "echo 'Stopping port-forwards...'; pkill -f 'port-forward'; exit" INT
while true; do sleep 1; done
