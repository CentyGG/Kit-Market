package com.example.kit_market.presentation.screen.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kit_market.domain.repository.CartRepository
import com.example.kit_market.domain.repository.OrderRepository
import com.example.kit_market.presentation.screen.orderdetail.OrderDetailScreen
import com.example.kit_market.presentation.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

data class PaymentWebViewScreen(
    val paymentUrl: String,
    val orderId: Long,
    val paymentId: String
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val orderRepository = koinInject<OrderRepository>()
        val cartRepository = koinInject<CartRepository>()
        var paymentResult by remember { mutableStateOf<PaymentResult?>(null) }
        var isConfirming by remember { mutableStateOf(false) }
        var confirmError by remember { mutableStateOf<String?>(null) }
        var isCheckingBeforeBack by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(paymentResult) {
            when (paymentResult) {
                PaymentResult.SUCCESS -> {
                    isConfirming = true
                    confirmError = null

                    // Оплата прошла — очищаем корзину сразу, независимо от подтверждения
                    try { cartRepository.clearCart() } catch (_: Exception) { }

                    // Retry confirmation up to 8 times with increasing delay
                    var confirmed = false
                    for (attempt in 1..8) {
                        try {
                            delay(when (attempt) {
                                1 -> 2000L
                                2 -> 3000L
                                3 -> 4000L
                                else -> 5000L
                            })
                            val status = orderRepository.confirmPayment(paymentId)
                            if (status == "paid" || status == "already_paid") {
                                confirmed = true
                                break
                            }
                        } catch (_: Exception) {
                            if (attempt == 8) break
                        }
                    }

                    isConfirming = false
                    if (confirmed) {
                        navigator.replace(OrderDetailScreen(orderId))
                    } else {
                        confirmError = "Оплата прошла, но не удалось подтвердить на сервере. Статус обновится автоматически."
                    }
                }
                PaymentResult.FAIL -> {
                    try {
                        orderRepository.cancelOrder(orderId)
                    } catch (_: Exception) { }
                }
                null -> { /* loading */ }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Оплата", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                // Проверяем, не прошла ли оплата, прежде чем уходить
                                if (paymentResult != null || isConfirming || isCheckingBeforeBack) return@IconButton
                                isCheckingBeforeBack = true
                                coroutineScope.launch {
                                    try {
                                        val status = orderRepository.getPaymentStatus(paymentId)
                                        if (status.status == "paid" || status.status == "confirmed") {
                                            // Оплата уже прошла — не уходим, а запускаем подтверждение
                                            paymentResult = PaymentResult.SUCCESS
                                        } else {
                                            navigator.pop()
                                        }
                                    } catch (_: Exception) {
                                        // Нет сети — безопасно уйти нельзя, предупреждаем
                                        confirmError = "Нет соединения. Невозможно проверить, прошла ли оплата. Проверьте интернет."
                                    }
                                    isCheckingBeforeBack = false
                                }
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = KitWhite,
                        titleContentColor = KitTextPrimary
                    )
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    isCheckingBeforeBack -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = KitBlue)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Проверяем статус оплаты...",
                                fontSize = 16.sp,
                                color = KitTextSecondary
                            )
                        }
                    }
                    isConfirming -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = KitBlue)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Подтверждаем оплату...",
                                fontSize = 16.sp,
                                color = KitTextSecondary
                            )
                        }
                    }
                    confirmError != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = confirmError!!,
                                fontSize = 16.sp,
                                color = KitTextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    confirmError = null
                                    if (paymentResult == PaymentResult.SUCCESS) {
                                        // Оплата точно прошла — повторяем подтверждение
                                        paymentResult = null
                                        paymentResult = PaymentResult.SUCCESS
                                    } else {
                                        // Не знаем, прошла ли оплата — проверяем статус заново
                                        isCheckingBeforeBack = true
                                        coroutineScope.launch {
                                            try {
                                                val status = orderRepository.getPaymentStatus(paymentId)
                                                if (status.status == "paid" || status.status == "confirmed") {
                                                    paymentResult = PaymentResult.SUCCESS
                                                } else {
                                                    // Не оплачено — можно безопасно уйти
                                                    navigator.pop()
                                                }
                                            } catch (_: Exception) {
                                                confirmError = "Нет соединения. Проверьте интернет и попробуйте снова."
                                            }
                                            isCheckingBeforeBack = false
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = KitBlue)
                            ) {
                                Text("Повторить", color = KitWhite)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = { navigator.replace(OrderDetailScreen(orderId)) }
                            ) {
                                Text(
                                    "Перейти к заказу",
                                    color = KitBlue,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    paymentResult == PaymentResult.FAIL -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Оплата не удалась, попробуйте снова",
                                fontSize = 16.sp,
                                color = KitTextSecondary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navigator.pop() },
                                colors = ButtonDefaults.buttonColors(containerColor = KitBlue)
                            ) {
                                Text("Назад", color = KitWhite)
                            }
                        }
                    }
                    else -> {
                        PlatformWebView(
                            url = paymentUrl,
                            onSuccess = { paymentResult = PaymentResult.SUCCESS },
                            onFail = { paymentResult = PaymentResult.FAIL }
                        )
                    }
                }
            }
        }
    }
}

enum class PaymentResult {
    SUCCESS, FAIL
}

@Composable
expect fun PlatformWebView(
    url: String,
    onSuccess: () -> Unit,
    onFail: () -> Unit
)
