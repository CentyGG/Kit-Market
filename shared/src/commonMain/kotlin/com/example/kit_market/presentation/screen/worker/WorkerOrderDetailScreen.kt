package com.example.kit_market.presentation.screen.worker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kit_market.domain.model.Order
import com.example.kit_market.domain.model.OrderStatus
import com.example.kit_market.domain.usecase.GetWorkerOrdersUseCase
import com.example.kit_market.domain.usecase.UpdateOrderStatusUseCase
import com.example.kit_market.domain.usecase.WorkerCancelOrderUseCase
import com.example.kit_market.presentation.common.formatPrice
import com.example.kit_market.presentation.theme.*
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class WorkerOrderDetailScreen(private val orderId: Long) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val getWorkerOrdersUseCase = koinInject<GetWorkerOrdersUseCase>()
        val updateOrderStatusUseCase = koinInject<UpdateOrderStatusUseCase>()
        val workerCancelOrderUseCase = koinInject<WorkerCancelOrderUseCase>()

        var order by remember { mutableStateOf<Order?>(null) }
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }
        var showCancelDialog by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(orderId) {
            try {
                val orders = getWorkerOrdersUseCase()
                order = orders.find { it.id == orderId }
                isLoading = false
            } catch (_: Exception) {
                error = "Не удалось загрузить заказ"
                isLoading = false
            }
        }

        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text("Отмена заказа") },
                text = { Text("Вы точно хотите отменить заказ #${orderId}? Если заказ был оплачен онлайн, деньги будут возвращены.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showCancelDialog = false
                            scope.launch {
                                try {
                                    val success = workerCancelOrderUseCase(orderId)
                                    if (success) navigator.pop()
                                } catch (_: Exception) {
                                    error = "Не удалось отменить заказ"
                                }
                            }
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

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Заказ #$orderId", fontWeight = FontWeight.Bold) },
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
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = KitBlue)
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(error!!, color = KitTextSecondary)
                    }
                }
                order != null -> {
                    val currentOrder = order!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Status
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = KitWhite),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Статус", fontSize = 14.sp, color = KitTextSecondary)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        StatusBadge(currentOrder.status)

                                        if (currentOrder.paymentType == "card" && currentOrder.status == OrderStatus.PAID) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFFE3F2FD)
                                            ) {
                                                Text(
                                                    text = "Оплачен онлайн",
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF1976D2)
                                                )
                                            }
                                        }
                                        if (currentOrder.paymentType == "cash") {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFFFFF3E0)
                                            ) {
                                                Text(
                                                    text = "Оплата наличными при получении",
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                    fontSize = 13.sp,
                                                    color = Color(0xFFE65100)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Pickup time
                            if (currentOrder.pickupTime != null) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = KitWhite),
                                        elevation = CardDefaults.cardElevation(2.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text("Время самовывоза", fontSize = 14.sp, color = KitTextSecondary)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = currentOrder.pickupTime!!,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF7B1FA2)
                                            )
                                        }
                                    }
                                }
                            }

                            // Items
                            item {
                                Text(
                                    text = "Товары",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KitTextPrimary
                                )
                            }

                            items(currentOrder.items) { item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = KitWhite),
                                    elevation = CardDefaults.cardElevation(1.dp)
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

                            // Total
                            item {
                                HorizontalDivider(color = KitGray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Итого", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = KitTextPrimary)
                                    Text(
                                        "${currentOrder.totalPrice.formatPrice()} \u20BD",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KitBlue
                                    )
                                }
                            }
                        }

                        // Action buttons at bottom
                        val (buttonText, nextStatus) = getNextAction(currentOrder.status)
                        val canCancel = currentOrder.status != OrderStatus.COMPLETED && currentOrder.status != OrderStatus.CANCELLED
                        if ((buttonText != null && nextStatus != null) || canCancel) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shadowElevation = 8.dp
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    if (buttonText != null && nextStatus != null) {
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    try {
                                                        val success = updateOrderStatusUseCase(orderId, nextStatus)
                                                        if (success) {
                                                            navigator.pop()
                                                        }
                                                    } catch (_: Exception) {
                                                        error = "Не удалось обновить статус"
                                                    }
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = KitBlue)
                                        ) {
                                            Text(
                                                buttonText,
                                                color = KitWhite,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                    if (canCancel) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = { showCancelDialog = true },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                                        ) {
                                            Text(
                                                "Отменить заказ",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium
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
    }
}

@Composable
private fun StatusBadge(status: OrderStatus) {
    val (backgroundColor, textColor) = when (status) {
        OrderStatus.CREATED -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        OrderStatus.PAID -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
        OrderStatus.ASSEMBLING -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        OrderStatus.READY -> Color(0xFFE8F5E9) to Color(0xFF388E3C)
        OrderStatus.COMPLETED -> Color(0xFFE8F5E9) to Color(0xFF1B5E20)
        OrderStatus.CANCELLED -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Text(
            text = status.toRussian(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

private fun getNextAction(status: OrderStatus): Pair<String?, String?> {
    return when (status) {
        OrderStatus.CREATED, OrderStatus.PAID -> "Взять в сборку" to "assembling"
        OrderStatus.ASSEMBLING -> "Готов к выдаче" to "ready"
        OrderStatus.READY -> "Выдан" to "completed"
        else -> null to null
    }
}
