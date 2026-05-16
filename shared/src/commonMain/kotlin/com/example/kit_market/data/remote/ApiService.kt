package com.example.kit_market.data.remote

import com.example.kit_market.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class ApiService(private val client: HttpClient) {

    suspend fun sendCode(phone: String): Boolean {
        val response = client.post("/auth/send-code") {
            setBody(AuthRequest(phone))
        }
        return response.status.value in 200..299
    }

    suspend fun verifyCode(phone: String, code: String): AuthResponse {
        return client.post("/auth/verify-code") {
            setBody(VerifyCodeRequest(phone, code))
        }.body()
    }

    suspend fun getProducts(): List<ProductResponse> {
        return client.get("/products").body()
    }


    suspend fun createOrder(items: List<OrderItemRequest>, paymentType: String = "cash", pickupTime: String? = null): OrderResponse {
        return client.post("/orders") {
            setBody(CreateOrderRequest(items, paymentType, pickupTime))
        }.body()
    }

    suspend fun getOrders(): List<OrderResponse> {
        return client.get("/orders").body()
    }

    suspend fun getOrderById(id: Long): OrderResponse {
        return client.get("/orders/$id").body()
    }

    suspend fun cancelOrder(orderId: Long): Boolean {
        val response = client.post("/orders/$orderId/cancel")
        return response.status.value in 200..299
    }

    suspend fun createPayment(orderId: Long): PaymentResponse {
        return client.post("/payments/create") {
            setBody(CreatePaymentRequest(orderId))
        }.body()
    }

    suspend fun getPaymentStatus(paymentId: String): PaymentStatusResponse {
        return client.get("/payments/status/$paymentId").body()
    }

    suspend fun confirmPayment(paymentId: String): PaymentConfirmResponse {
        return client.post("/payments/confirm/$paymentId").body()
    }

    suspend fun getReceipt(orderId: Long): ReceiptDetailResponse {
        return client.get("/receipts/order/$orderId").body()
    }

    suspend fun getUser(): UserResponse {
        return client.get("/user").body()
    }

    suspend fun updateUser(name: String): UserResponse {
        return client.put("/user") {
            setBody(UpdateUserRequest(name))
        }.body()
    }

    // Worker endpoints
    suspend fun getWorkerOrders(status: String? = null): List<OrderResponse> {
        return client.get("/worker/orders") {
            if (status != null) parameter("status", status)
        }.body()
    }

    suspend fun updateOrderStatus(orderId: Long, status: String): Boolean {
        val response = client.put("/orders/$orderId/status") {
            setBody(UpdateStatusRequest(status))
        }
        return response.status.value in 200..299
    }

    suspend fun workerCancelOrder(orderId: Long): Boolean {
        val response = client.post("/orders/$orderId/worker-cancel")
        return response.status.value in 200..299
    }
}
