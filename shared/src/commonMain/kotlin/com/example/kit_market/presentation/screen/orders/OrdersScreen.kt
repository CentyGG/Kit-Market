package com.example.kit_market.presentation.screen.orders

import androidx.compose.foundation.clickable
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
import org.koin.compose.koinInject
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kit_market.domain.model.Order
import com.example.kit_market.domain.model.OrderStatus
import com.example.kit_market.presentation.screen.orderdetail.OrderDetailScreen
import com.example.kit_market.presentation.theme.*

class OrdersScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinInject<OrdersViewModel>()
        val state by screenModel.state.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Заказы", fontWeight = FontWeight.Bold) },
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
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = KitBlue)
                }
            } else if (state.orders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("У вас пока нет заказов", fontSize = 16.sp, color = KitTextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.orders, key = { it.id }) { order ->
                        OrderCard(
                            order = order,
                            onClick = { navigator.push(OrderDetailScreen(order.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order, onClick: () -> Unit) {
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
                StatusChip(order.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = order.date,
                fontSize = 14.sp,
                color = KitTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Итого",
                    fontSize = 14.sp,
                    color = KitTextSecondary
                )
                Text(
                    text = "${"%.2f".format(order.totalPrice)} \u20BD",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = KitTextPrimary
                )
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
