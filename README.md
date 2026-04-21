# 💳 GPay Payment Flow Demo (Spring Boot)

This project demonstrates a simplified end-to-end payment flow using a Spring Boot backend and a basic frontend. It simulates how a real-world Google Pay (GPay) integration works, including payment processing, status tracking, webhook handling, and duplicate request protection.

---

## 🚀 Features

- Payment creation API  
- Payment status tracking  
- Webhook simulation (gateway callback)  
- Duplicate request protection using idempotency key  
- Simple frontend with Pay button  
- Supports payment states:
  - SUCCESS  
  - FAILED  
  - PENDING  

---

## 🏗️ Architecture Overview

User → Frontend → Spring Boot Backend → Payment Gateway (Simulated)

---

## 🔄 Payment Flow (Step-by-Step)

1. User clicks Pay Now on the frontend  
2. Frontend sends payment request to backend  
3. Backend validates request and creates a payment record  
4. Backend assigns a unique paymentId  
5. Backend simulates sending request to payment gateway  
6. Gateway returns response: SUCCESS, FAILED, or PENDING  
7. Backend updates payment status  
8. If payment is PENDING, webhook updates status later  

---

## 📡 API Endpoints

Create Payment  
POST /api/payments/gpay  

Request Example:
{
  "orderId": "ORD-1001",
  "amount": 25.00,
  "idempotencyKey": "abc123"
}

Response Example:
{
  "paymentId": "uuid",
  "status": "SUCCESS",
  "currency": "USD"
}

Get Payment Status  
GET /api/payments/{paymentId}

Webhook Simulation  
POST /api/payments/webhook/{paymentId}

---

## 💡 Payment Status

SUCCESS → Payment completed  
FAILED → Payment failed  
PENDING → Waiting for confirmation  

---

## ⚠️ Edge Case Handling

- Duplicate requests → handled using idempotency key  
- Retry logic → safe retries only  
- Pending payments → updated via webhook  
- Duplicate webhook → ignored  

---

## 🖥️ Frontend

Simple HTML page with:
- Pay Now button  
- Check Payment Status  
- Simulate Webhook  

---

## 🛠️ Tech Stack

- Java 17  
- Spring Boot  
- REST APIs  
- HTML + JavaScript  

---

## ⚠️ Note

This is a simulation project for learning and interview purposes. No real payment gateway is integrated.

---

## ▶️ How to Run

./mvnw spring-boot:run  

Open:  
http://localhost:8081  

---

## 🎯 Interview Talking Points

- End-to-end payment flow  
- REST API design  
- Webhook handling  
- Idempotency for duplicate protection  
- Payment status lifecycle  

---

## 👤 Author

Krishana Thapa