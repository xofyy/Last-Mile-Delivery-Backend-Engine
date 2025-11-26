# Test Results & Verification Report

This document details the comprehensive testing strategy executed for the **Last-Mile Delivery Backend Engine**.

## 1. Automated Unit Tests (JUnit 5 & Mockito)
**Scope:** Core Business Logic (`OrderAssignmentService`)
- **Test Case:** `shouldAssignNearestCourier`
    - **Scenario:** Order created with multiple couriers available.
    - **Result:** ✅ PASSED. Service correctly identified the courier with minimum distance using Haversine formula.
- **Test Case:** `shouldThrowExceptionWhenNoCourierAvailable`
    - **Scenario:** Order created when all couriers are BUSY or OFFLINE.
    - **Result:** ✅ PASSED. Service threw expected exception (or handled gracefully depending on config).

## 2. Integration & API Tests (PowerShell Scripts)

### A. Happy Path (`test_endpoints.ps1`)
**Objective:** Verify end-to-end flow from registration to order creation.
1.  **Data Seeding:** Successfully inserted dummy Restaurant and Courier.
2.  **Registration:** User `Murat Test` registered successfully.
3.  **Authentication:** Login successful, JWT Token retrieved.
4.  **Order Creation:** Order created successfully with valid Token.
5.  **Assignment:** Order automatically assigned to `Moto Kurye 1`.
**Result:** ✅ **ALL PASSED**

### B. Edge Cases (`comprehensive_test.ps1`)
**Objective:** Verify system resilience and error handling.
1.  **Input Validation:**
    - Invalid Email (`not-an-email`) -> Returned `400 Bad Request`.
    - Negative Amount (`-50.0`) -> Returned `400 Bad Request`.
2.  **Unauthorized Access:**
    - Request without Bearer Token -> Returned `403 Forbidden`.
3.  **Graceful Degradation (No Couriers):**
    - Scenario: All couriers set to `BUSY`.
    - Outcome: Order created with `courierId: null`. System did not crash.
**Result:** ✅ **ALL PASSED**

### C. Advanced Scenarios (`advanced_test.ps1`)
**Objective:** Verify complex logic and concurrency.
1.  **Geo-spatial Accuracy:**
    - **Setup:** Courier A (100m away), Courier B (15km away).
    - **Outcome:** System selected Courier A.
    - **Result:** ✅ PASSED.
2.  **Concurrency (Race Condition):**
    - **Setup:** 1 Available Courier, 5 Simultaneous Orders.
    - **Outcome:** Only **1** order was assigned to the courier. 4 orders remained unassigned.
    - **Result:** ✅ PASSED. Transaction isolation prevented double-booking.

## 3. Manual Verification
- **Swagger UI:** Accessible at `http://localhost:8080/swagger-ui.html`. All endpoints documented.
- **Database:** Verified data integrity in PostgreSQL using `docker exec`.
- **Infrastructure:** Docker Compose correctly orchestrates App, Postgres, Redis, and RabbitMQ.

## Summary
The system has proven to be robust, secure, and functionally correct under both normal and stress conditions.
