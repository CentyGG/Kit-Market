package com.example.kit_market.presentation.screen.orderdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kit_market.domain.model.OrderItem
import com.example.kit_market.domain.model.OrderStatus
import com.example.kit_market.presentation.common.formatPrice
import com.example.kit_market.presentation.screen.receipt.ReceiptScreen
import com.example.kit_market.presentation.theme.*
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import androidx.compose.material.icons.filled.Refresh

data class OrderDetailScreen(val orderId: Long) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinInject<OrderDetailViewModel>(parameters = { parametersOf(orderId) })
        val state by viewModel.state.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Заказ #${orderId}",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refreshOrder() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Обновить", tint = KitBlue)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = KitWhite,
                        titleContentColor = KitTextPrimary
                    )
                )
            }
        ) { padding ->
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = KitBlue)
                    }
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.error!!,
                            fontSize = 16.sp,
                            color = KitTextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.retry() },
                            colors = ButtonDefaults.buttonColors(containerColor = KitBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Повторить", color = KitWhite)
                        }
                    }
                }
                state.order != null -> {
                    val order = state.order!!
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Order info card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = KitWhite),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Заказ #${order.id}",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = KitTextPrimary
                                        )
                                        StatusChip(order.status)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Дата: ${order.date}",
                                        fontSize = 14.sp,
                                        color = KitTextSecondary
                                    )
                                    if (order.pickupTime != null) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFF3E5F5)
                                        ) {
                                            Text(
                                                text = "Самовывоз: ${order.pickupTime}",
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF7B1FA2)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Ожидание подтверждения оплаты с таймером
                        if (order.status == OrderStatus.CREATED && order.paymentType == "card") {
                            item {
                                val timeLeft = state.paymentTimeLeftSeconds
                                val timerText = if (timeLeft != null && timeLeft > 0) {
                                    val minutes = timeLeft / 60
                                    val seconds = timeLeft % 60
                                    "Ожидание оплаты: %02d:%02d".format(minutes, seconds)
                                } else {
                                    "Ожидание подтверждения оплаты..."
                                }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp,
                                                color = Color(0xFFE65100)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = timerText,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFFE65100)
                                            )
                                        }
                                        if (timeLeft != null && timeLeft > 0) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Заказ будет отменён, если не оплатить вовремя",
                                                fontSize = 12.sp,
                                                color = Color(0xFFBF360C)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Ready banner
                        if (order.status == OrderStatus.READY) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                                ) {
                                    Text(
                                        text = "Ваш заказ готов к выдаче",
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF388E3C),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // Items header
                        item {
                            Text(
                                text = "Товары",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = KitTextPrimary
                            )
                        }

                        // Order items
                        items(order.items) { item ->
                            OrderItemCard(item)
                        }

                        // Total
                        item {
                            HorizontalDivider(color = KitGray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Итого",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KitTextPrimary
                                )
                                Text(
                                    text = "${order.totalPrice.formatPrice()} \u20BD",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KitBlue
                                )
                            }
                        }

                        // Receipt button
                        if (order.hasReceipt) {
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = { navigator.push(ReceiptScreen(order.id)) },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = KitBlue)
                                ) {
                                    Text("Посмотреть чек", fontSize = 15.sp)
                                }
                            }
                        }

                        // Cancel button (only for CREATED and PAID)
                        if (order.status == OrderStatus.CREATED || order.status == OrderStatus.PAID) {
                            item {
                                var showCancelDialog by remember { mutableStateOf(false) }

                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = { showCancelDialog = true },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFD32F2F)
                                    ),
                                    enabled = !state.isCancelling
                                ) {
                                    if (state.isCancelling) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp,
                                            color = Color(0xFFD32F2F)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Отмена...", fontSize = 15.sp)
                                    } else {
                                        Text("Отменить заказ", fontSize = 15.sp)
                                    }
                                }

                                if (showCancelDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showCancelDialog = false },
                                        title = { Text("Отменить заказ?") },
                                        text = {
                                            Text(
                                                if (order.status == OrderStatus.PAID && order.paymentType == "card")
                                                    "Деньги вернутся на карту в течение 1–5 рабочих дней."
                                                else
                                                    "Вы уверены, что хотите отменить заказ #${order.id}?"
                                            )
                                        },
                                        confirmButton = {
                                            TextButton(
                                                onClick = {
                                                    showCancelDialog = false
                                                    viewModel.cancelOrder()
                                                }
                                            ) {
                                                Text("Отменить заказ", color = Color(0xFFD32F2F))
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = { showCancelDialog = false }) {
                                                Text("Нет", color = KitBlue)
                                            }
                                        }
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: OrderStatus) {
    val (backgroundColor, textColor) = when (status) {
        OrderStatus.CREATED -> KitGrayLight to KitTextSecondary
        OrderStatus.PAID -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
        OrderStatus.ASSEMBLING -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        OrderStatus.READY -> Color(0xFFE8F5E9) to Color(0xFF388E3C)
        OrderStatus.COMPLETED -> Color(0xFFE8F5E9) to Color(0xFF1B5E20)
        OrderStatus.CANCELLED -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
    }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = status.toRussian(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun OrderItemCard(item: OrderItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = KitWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = KitTextPrimary
                )
                Text(
                    text = "${item.quantity} шт. x ${item.price.formatPrice()} \u20BD",
                    fontSize = 12.sp,
                    color = KitTextSecondary
                )
            }
            Text(
                text = "${(item.price * item.quantity).formatPrice()} \u20BD",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = KitTextPrimary
            )
        }
    }
}
