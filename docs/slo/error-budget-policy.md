# Error Budget Policy

Bu doküman, Delivery Backend Engine için Service Level Objectives (SLO) ve Error Budget yönetim politikasını tanımlar.

---

## 📊 SLO Tanımları

### Availability SLO

| Metrik | Hedef | Pencere | Hesaplama |
|--------|-------|---------|-----------|
| **Availability** | 99.9% | 30 gün | `(başarılı istekler / toplam istekler)` |

**Başarılı istek:** HTTP status kodu 5xx olmayan tüm istekler (1xx, 2xx, 3xx, 4xx).

### Latency SLO

| Metrik | Hedef | Pencere | Hesaplama |
|--------|-------|---------|-----------|
| **P99 Latency** | < 500ms | 30 gün | `%99 isteklerin 500ms altında tamamlanması` |

---

## 💰 Error Budget Hesaplaması

### 30 Günlük Error Budget

| SLO | Hedef | Error Budget | İzin Verilen Downtime |
|-----|-------|--------------|----------------------|
| Availability 99.9% | %0.1 hata | 43.2 dakika/ay | ~1.4 dakika/gün |
| Latency 99% | %1 yavaş istek | 432 dakika/ay | ~14.4 dakika/gün |

### Hesaplama Formülü

```
Error Budget = 1 - SLO Target
             = 1 - 0.999
             = 0.001 (0.1%)

30 günde izin verilen hata süresi:
= 30 gün × 24 saat × 60 dakika × 0.001
= 43.2 dakika
```

---

## 🚦 Error Budget Durumuna Göre Aksiyonlar

### Budget Durumu Tablosu

| Kalan Budget | Durum | Renk | Aksiyon |
|--------------|-------|------|---------|
| **> 50%** | Sağlıklı | 🟢 | Normal geliştirme devam eder |
| **25-50%** | Dikkat | 🟡 | Yalnızca düşük riskli değişiklikler |
| **10-25%** | Uyarı | 🟠 | Ekstra code review, staging test zorunlu |
| **< 10%** | Kritik | 🔴 | Feature freeze, sadece güvenilirlik çalışması |
| **0% (Tükendi)** | Donduruldu | ⚫ | Tam freeze, incident review zorunlu |

---

## 🔥 Burn Rate Alerting

Google SRE metodolojisine göre multi-window burn rate alerting kullanıyoruz:

### Fast Burn Alert (Kritik)

| Parametre | Değer |
|-----------|-------|
| Burn Rate | 14.4x |
| Pencere | 1 saat |
| Budget Tükenme Süresi | ~2 saat |
| Severity | 🔴 Critical |

**Aksiyon:** Hemen müdahale et. On-call mühendis uyandırılır.

### Slow Burn Alert (Uyarı)

| Parametre | Değer |
|-----------|-------|
| Burn Rate | 3x |
| Pencere | 6 saat |
| Budget Tükenme Süresi | ~10 gün |
| Severity | 🟡 Warning |

**Aksiyon:** Mesai saatlerinde incele. Root cause analizi yap.

---

## 📋 SLO Review Süreci

### Haftalık Review

Her Pazartesi:
1. Geçen haftanın SLI metriklerini incele
2. Error budget tüketimini kontrol et
3. Trend analizi yap (iyileşme/kötüleşme)

### Aylık Review

Her ayın ilk haftası:
1. 30 günlük SLO performansını değerlendir
2. Error budget kullanımını raporla
3. Gerekirse SLO hedeflerini revize et

---

## 🎯 SLO vs SLA

| Terim | Tanım | Kim İçin? |
|-------|-------|-----------|
| **SLI** (Service Level Indicator) | Ölçülen metrik | Engineering |
| **SLO** (Service Level Objective) | İç hedef | Engineering |
| **SLA** (Service Level Agreement) | Müşteri sözleşmesi | İş/Hukuk |

> ⚠️ **Önemli:** SLO'lar her zaman SLA'lardan daha sıkı olmalıdır. Bu sayede SLA ihlali öncesinde uyarı alırsınız.

---

## 📈 Prometheus Queries

### Anlık Availability
```promql
sli:availability:ratio_5m
```

### 30 Günlük Availability
```promql
sli:availability:ratio_30d
```

### Kalan Error Budget
```promql
slo:availability:error_budget_remaining
```

### Burn Rate
```promql
slo:availability:burn_rate_1h
```

---

## 🔗 İlgili Dokümanlar

- [Runbook: High Error Rate](../runbooks/high-error-rate.md)
- [Runbook: High Latency](../runbooks/high-latency.md)
- [Google SRE Book - Error Budgets](https://sre.google/sre-book/error-budget/)
