package com.example.kit_market.presentation.screen.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kit_market.domain.repository.OrderRepository
import com.example.kit_market.presentation.screen.orderdetail.OrderDetailScreen
import com.example.kit_market.presentation.theme.*
import kotlinx.coroutines.delay
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
        var paymentResult by remember { mutableStateOf<PaymentResult?>(null) }
        var isConfirming by remember { mutableStateOf(false) }

        LaunchedEffect(paymentResult) {
            when (paymentResult) {
                PaymentResult.SUCCESS -> {
                    isConfirming = true
                    try {
                        // Даём Tinkoff пару секунд на обработку
                        delay(2000)
                        orderRepository.confirmPayment(paymentId)
                    } catch (_: Exception) {
                        // Даже если confirm упал — переходим к заказу
                    }
                    isConfirming = false
                    navigator.replace(OrderDetailScreen(orderId))
                }
                PaymentResult.FAIL -> { /* stay, show error */ }
                null -> { /* loading */ }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Оплата", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
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
