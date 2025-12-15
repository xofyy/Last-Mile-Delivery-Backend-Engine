# Runbook: High Error Rate

## Alert Name
`HighErrorRate`

## Severity
**Critical** 🔴

## Symptom
API error rate (5xx responses) exceeds 5% for more than 5 minutes.

---

## Quick Diagnosis (2 min)

### 1. Check Pod Status
```bash
kubectl get pods -n default -l app=delivery-app-backend
```

### 2. View Recent Logs
```bash
kubectl logs -l app=delivery-app-backend --tail=100 --since=10m | grep -i error
```

### 3. Check Grafana Dashboard
```bash
kubectl port-forward svc/loki-grafana -n monitoring 3000:80
# Open: http://localhost:3000
```

---

## Common Causes & Remediation

### 1. Database Connection Issues
**Symptoms:** `Connection refused`, `timeout`, `too many connections`

```bash
# Check PostgreSQL pod
kubectl get pods | grep postgres
kubectl logs delivery-postgres-0 --tail=50

# Restart if needed
kubectl rollout restart statefulset/delivery-postgres
```

### 2. Out of Memory (OOM)
**Symptoms:** Pod restarts, `OOMKilled` in events

```bash
# Check events
kubectl get events --sort-by='.lastTimestamp' | head -20

# Check memory usage
kubectl top pods

# Increase limits (edit values-prod.yaml)
# backend.resources.limits.memory: "2048Mi"
```

### 3. Upstream Service Failure (AI Service)
**Symptoms:** Errors calling `/predict` endpoint

```bash
# Check AI service status
kubectl get pods -l app=delivery-app-ai
kubectl logs -l app=delivery-app-ai --tail=50
```

### 4. RabbitMQ Connection Lost
**Symptoms:** `Connection reset`, message publishing failures

```bash
# Check RabbitMQ
kubectl get pods | grep rabbitmq
kubectl exec -it delivery-rabbitmq-0 -- rabbitmqctl status
```

---

## Escalation

| Time Elapsed | Action |
|--------------|--------|
| 0-5 min | Follow this runbook |
| 5-15 min | Notify #platform-oncall |
| 15+ min | Page on-call engineer |

## Post-Incident
1. Create incident report
2. Update this runbook if new failure mode discovered
3. Consider adding new alert for root cause
