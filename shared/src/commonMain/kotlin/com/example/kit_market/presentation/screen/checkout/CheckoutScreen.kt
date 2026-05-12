package com.example.kit_market.presentation.screen.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kit_market.domain.model.CartItem
import com.example.kit_market.presentation.screen.orderdetail.OrderDetailScreen
import com.example.kit_market.presentation.screen.payment.PaymentWebViewScreen
import com.example.kit_market.presentation.theme.*
import org.koin.compose.koinInject

class CheckoutScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinInject<CheckoutViewModel>()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(state.orderCreatedId, state.paymentUrl) {
            val orderId = state.orderCreatedId ?: return@LaunchedEffect
            if (state.paymentMethod == PaymentMethod.CARD && state.paymentUrl != null && state.paymentId != null) {
                navigator.replace(PaymentWebViewScreen(state.paymentUrl!!, orderId, state.paymentId!!))
            } else if (state.paymentMethod == PaymentMethod.CASH) {
                navigator.replace(OrderDetailScreen(orderId))
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Оформление заказа", fontWeight = FontWeight.Bold) },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Товары",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KitTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(state.items) { item ->
                        CheckoutItemRow(item)
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = KitGray)
                        Spacer(modifier = Modifier.height(16.dp))

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
                                text = "${"%.2f".format(state.totalPrice)} \u20BD",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = KitBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Способ оплаты",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KitTextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        PaymentMethodOption(
                            text = "Наличные при получении",
                            selected = state.paymentMethod == PaymentMethod.CASH,
                            onClick = { viewModel.onIntent(CheckoutIntent.SelectPaymentMethod(PaymentMethod.CASH)) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PaymentMethodOption(
                            text = "Картой онлайн",
                            selected = state.paymentMethod == PaymentMethod.CARD,
                            onClick = { viewModel.onIntent(CheckoutIntent.SelectPaymentMethod(PaymentMethod.CARD)) }
                        )
                    }

                    if (state.error != null) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.error!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
                    Button(
                        onClick = { viewModel.onIntent(CheckoutIntent.PlaceOrder) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KitBlue),
                        enabled = !state.isLoading && state.items.isNotEmpty()
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color = KitWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                "Оформить заказ",
                                color = KitWhite,
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

@Composable
private fun CheckoutItemRow(item: CartItem) {
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
                    text = item.product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = KitTextPrimary
                )
                Text(
                    text = "${item.quantity} шт. x ${"%.2f".format(item.product.price)} \u20BD",
                    fontSize = 12.sp,
                    color = KitTextSecondary
                )
            }
            Text(
                text = "${"%.2f".format(item.product.price * item.quantity)} \u20BD",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = KitTextPrimary
            )
        }
    }
}

@Composable
private fun PaymentMethodOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) KitBlueLight else KitWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = KitBlue)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                color = KitTextPrimary
            )
        }
    }
}
