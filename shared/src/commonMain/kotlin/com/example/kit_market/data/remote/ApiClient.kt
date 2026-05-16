package com.example.kit_market.data.remote

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ApiClient {

    const val BASE_URL = "http://159.194.229.53:8080"

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 30_000
            socketTimeoutMillis = 15_000
        }
        defaultRequest {
            url(BASE_URL)
            contentType(ContentType.Application.Json)
            TokenStorage.token?.let { token ->
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }
}
