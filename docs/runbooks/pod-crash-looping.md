# Runbook: Pod Crash Looping

## Alert Name
`PodCrashLooping`

## Severity
**Critical** 🔴

## Symptom
A pod has restarted more than 3 times in the last 15 minutes.

---

## Quick Diagnosis (2 min)

### 1. Identify the Crashing Pod
```bash
kubectl get pods -n default --sort-by='.status.containerStatuses[0].restartCount'
```

### 2. Check Pod Events
```bash
kubectl describe pod <POD_NAME> | tail -30
```

### 3. View Crash Logs
```bash
# Current logs
kubectl logs <POD_NAME> --tail=100

# Previous container logs (before crash)
kubectl logs <POD_NAME> --previous --tail=100
```

---

## Common Causes & Remediation

### 1. OOMKilled (Out of Memory)
**Symptoms:** `Reason: OOMKilled` in describe output

```bash
# Check current memory limits
kubectl get pod <POD_NAME> -o jsonpath='{.spec.containers[*].resources}'

# Solution: Increase memory limits in Helm values
# Edit k8s/charts/delivery-app/values-prod.yaml
# backend.resources.limits.memory: "2048Mi"

# Apply changes
helm upgrade delivery-app ./k8s/charts/delivery-app -f ./k8s/charts/delivery-app/values-prod.yaml
```

### 2. Liveness Probe Failure
**Symptoms:** `Liveness probe failed` in events

```bash
# Check probe configuration
kubectl get pod <POD_NAME> -o jsonpath='{.spec.containers[*].livenessProbe}'

# Test health endpoint manually
kubectl exec -it <POD_NAME> -- curl -s http://localhost:8080/actuator/health/liveness
```

**Solution:** Increase `initialDelaySeconds` or `failureThreshold` in deployment.yaml

### 3. Application Error on Startup
**Symptoms:** Error messages in logs on startup

```bash
# Check startup logs
kubectl logs <POD_NAME> --previous | head -100
```

**Common fixes:**
- Missing environment variables → Check ConfigMap/Secrets
- Database not ready → Check PostgreSQL pod
- Wrong configuration → Review application.yaml

### 4. Image Pull Error
**Symptoms:** `ErrImagePull` or `ImagePullBackOff`

```bash
# Check image
kubectl describe pod <POD_NAME> | grep Image

# Verify image exists
gcloud artifacts docker images list us-central1-docker.pkg.dev/<PROJECT>/delivery-repo
```

---

## Quick Fixes

### Force Restart Deployment
```bash
kubectl rollout restart deployment/delivery-app-backend
```

### Scale Down/Up
```bash
kubectl scale deployment/delivery-app-backend --replicas=0
sleep 10
kubectl scale deployment/delivery-app-backend --replicas=1
```

### Rollback to Previous Version
```bash
kubectl rollout undo deployment/delivery-app-backend
```

---

## Escalation

| Time Elapsed | Action |
|--------------|--------|
| 0-5 min | Follow this runbook |
| 5-10 min | Try rollback if recent deployment |
| 10+ min | Page on-call engineer |
