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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kit_market.domain.model.OrderItem
import com.example.kit_market.domain.model.OrderStatus
import com.example.kit_market.presentation.screen.receipt.ReceiptScreen
import com.example.kit_market.presentation.theme.*
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

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
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.error!!,
                            fontSize = 16.sp,
                            color = KitTextSecondary
                        )
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
                                    text = "${"%.2f".format(order.totalPrice)} \u20BD",
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
        OrderStatus.READY -> Color(0xFFE8F5E9) to Color(0xFF388E3C)
        OrderStatus.COMPLETED -> Color(0xFFE8F5E9) to Color(0xFF388E3C)
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
                    text = "${item.quantity} шт. x ${"%.2f".format(item.price)} \u20BD",
                    fontSize = 12.sp,
                    color = KitTextSecondary
                )
            }
            Text(
                text = "${"%.2f".format(item.price * item.quantity)} \u20BD",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = KitTextPrimary
            )
        }
    }
}
