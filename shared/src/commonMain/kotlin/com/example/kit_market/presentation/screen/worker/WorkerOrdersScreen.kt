package com.example.kit_market.presentation.screen.worker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import com.example.kit_market.presentation.common.formatPrice
import com.example.kit_market.presentation.theme.*
import org.koin.compose.koinInject

class WorkerOrdersScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinInject<WorkerOrdersViewModel>()
        val state by viewModel.state.collectAsState()
        var cancelOrderId by remember { mutableStateOf<Long?>(null) }

        LaunchedEffect(Unit) {
            viewModel.refresh()
        }

        // Диалог подтверждения отмены
        if (cancelOrderId != null) {
            AlertDialog(
                onDismissRequest = { cancelOrderId = null },
                title = { Text("Отмена заказа") },
                text = { Text("Вы точно хотите отменить заказ #${cancelOrderId}? Если заказ был оплачен онлайн, деньги будут возвращены.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.cancelOrder(cancelOrderId!!)
                            cancelOrderId = null
                        }
                    ) {
                        Text("Отменить заказ", color = Color(0xFFD32F2F))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { cancelOrderId = null }) {
                        Text("Нет", color = KitBlue)
                    }
                }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Управление заказами", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { viewModel.refresh() }) {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Tab row
                TabRow(
                    selectedTabIndex = WorkerTab.entries.indexOf(state.selectedTab),
                    containerColor = KitWhite,
                    contentColor = KitBlue
                ) {
                    WorkerTab.entries.forEach { tab ->
                        Tab(
                            selected = state.selectedTab == tab,
                            onClick = { viewModel.selectTab(tab) },
                            text = {
                                Text(
                                    text = tab.title,
                                    fontSize = 13.sp,
                                    fontWeight = if (state.selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = KitBlue)
                    }
                } else if (state.error != null) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(state.error!!, fontSize = 16.sp, color = KitTextSecondary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.refresh() },
                            colors = ButtonDefaults.buttonColors(containerColor = KitBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Повторить", color = KitWhite)
                        }
                    }
                } else if (state.orders.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Нет заказов", fontSize = 16.sp, color = KitTextSecondary)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.orders, key = { it.id }) { order ->
                            WorkerOrderCard(
                                order = order,
                                tab = state.selectedTab,
                                onStatusChange = { orderId, newStatus ->
                                    viewModel.updateStatus(orderId, newStatus)
                                },
                                onCancel = { orderId -> cancelOrderId = orderId },
                                onClick = {
                                    navigator.push(WorkerOrderDetailScreen(order.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkerOrderCard(
    order: Order,
    tab: WorkerTab,
    onStatusChange: (Long, String) -> Unit,
    onCancel: (Long) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = KitTextPrimary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (order.status == OrderStatus.PAID) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFE3F2FD)
                        ) {
                            Text(
                                text = "Оплачен",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }
                    if (order.paymentType == "cash" && order.status == OrderStatus.CREATED) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFFF3E0)
                        ) {
                            Text(
                                text = "Наличные",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Items summary
            val itemsSummary = order.items.joinToString(", ") { "${it.productName} x${it.quantity}" }
            Text(
                text = itemsSummary,
                fontSize = 13.sp,
                color = KitTextSecondary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${order.totalPrice.formatPrice()} \u20BD",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = KitTextPrimary
                )
                if (order.pickupTime != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF3E5F5)
                    ) {
                        Text(
                            text = "\u23F0 ${order.pickupTime}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 12.sp,
                            color = Color(0xFF7B1FA2)
                        )
                    }
                }
            }

            // Action button
            val (buttonText, nextStatus) = when (tab) {
                WorkerTab.NEW -> "В сборку" to "assembling"
                WorkerTab.ASSEMBLING -> "Готов" to "ready"
                WorkerTab.READY -> "Выдан" to "completed"
                WorkerTab.HISTORY -> null to null
            }

            if (tab != WorkerTab.HISTORY) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (buttonText != null && nextStatus != null) {
                        Button(
                            onClick = { onStatusChange(order.id, nextStatus) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KitBlue)
                        ) {
                            Text(buttonText, color = KitWhite, fontWeight = FontWeight.Medium)
                        }
                    }
                    OutlinedButton(
                        onClick = { onCancel(order.id) },
                        modifier = if (buttonText != null) Modifier else Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                    ) {
                        Text("Отменить", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
