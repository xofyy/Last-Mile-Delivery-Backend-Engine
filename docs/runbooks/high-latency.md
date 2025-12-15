# Runbook: High Latency

## Alert Name
`HighLatencyP99` / `HighLatencyP50`

## Severity
**Warning** 🟡

## Symptom
- P99 latency exceeds 1 second
- P50 (median) latency exceeds 500ms

---

## Quick Diagnosis (2 min)

### 1. Check Current Latency in Prometheus
```bash
kubectl port-forward svc/loki-prometheus-server -n monitoring 9090:80
# Visit: http://localhost:9090
# Query: histogram_quantile(0.99, rate(http_server_requests_seconds_bucket{application="delivery-backend"}[5m]))
```

### 2. Identify Slow Endpoints
```bash
# Query in Prometheus
topk(5, histogram_quantile(0.99, rate(http_server_requests_seconds_bucket{application="delivery-backend"}[5m])) by (uri))
```

### 3. Check Resource Usage
```bash
kubectl top pods -l app=delivery-app-backend
```

---

## Common Causes & Remediation

### 1. Database Slow Queries
**Symptoms:** Latency spikes correlate with DB operations

```bash
# Check DB connection pool
# Prometheus query: hikaricp_connections_active

# Check for slow queries in PostgreSQL
kubectl exec -it delivery-postgres-0 -- psql -U postgres -c "
SELECT pid, now() - pg_stat_activity.query_start AS duration, query
FROM pg_stat_activity
WHERE (now() - pg_stat_activity.query_start) > interval '1 second';"
```

**Solution:** 
- Add missing indexes
- Optimize N+1 queries
- Increase connection pool size

### 2. Cold Start / JVM Warmup
**Symptoms:** High latency right after deployment

```bash
# Check when pod started
kubectl get pods -o wide
```

**Solution:** Wait 2-3 minutes for JVM to warm up. Consider adding warmup endpoint.

### 3. GC Pauses
**Symptoms:** Periodic latency spikes

```bash
# Check JVM metrics
# Prometheus query: jvm_gc_pause_seconds_max
```

**Solution:** Tune JVM heap size and GC settings in deployment.yaml

### 4. External Service Slow (AI Service, Redis)
```bash
# Check AI service latency
kubectl logs -l app=delivery-app-ai --tail=50

# Check Redis latency
kubectl exec -it delivery-redis-0 -- redis-cli --latency
```

### 5. High Load / Insufficient Resources
```bash
# Check HPA status
kubectl get hpa

# Check if at max replicas
kubectl describe hpa delivery-app-backend-hpa
```

**Solution:** Increase HPA max replicas or resource limits

---

## Quick Mitigations

### Scale Up
```bash
kubectl scale deployment/delivery-app-backend --replicas=3
```

### Enable/Check Caching
```bash
# Verify Redis is working
kubectl exec -it delivery-redis-0 -- redis-cli ping
```

---

## Escalation

| Latency Level | Action |
|---------------|--------|
| P99 > 1s | Monitor for 10 min |
| P99 > 2s | Investigate actively |
| P99 > 5s | Page on-call |
