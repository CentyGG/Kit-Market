package com.example.kit_market.data.remote

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ApiClient {

    const val BASE_URL = "http://172.18.0.1:8080"

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
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
