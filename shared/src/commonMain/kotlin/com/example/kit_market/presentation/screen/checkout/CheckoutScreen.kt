package com.example.kit_market.presentation.screen.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kit_market.domain.model.CartItem
import com.example.kit_market.presentation.screen.orderdetail.OrderDetailScreen
import com.example.kit_market.presentation.screen.payment.PaymentWebViewScreen
import com.example.kit_market.presentation.common.formatPrice
import com.example.kit_market.presentation.theme.*
import kotlinx.datetime.*
import org.koin.compose.koinInject

class CheckoutScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinInject<CheckoutViewModel>()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(state.orderCreatedId, state.paymentUrl, state.orderPaid) {
            val orderId = state.orderCreatedId ?: return@LaunchedEffect
            if (state.orderPaid) {
                // Заказ уже оплачен — переходим к деталям, не создаём новый платёж
                navigator.replace(OrderDetailScreen(orderId))
            } else if (state.paymentMethod == PaymentMethod.CARD && state.paymentUrl != null && state.paymentId != null) {
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
                                text = "${state.totalPrice.formatPrice()} \u20BD",
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

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Время самовывоза",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KitTextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Date chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.availableDates) { date ->
                                val isSelected = date == state.selectedDate
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.onIntent(CheckoutIntent.SelectPickupDate(date)) },
                                    label = {
                                        Text(
                                            text = formatDateLabel(date),
                                            fontSize = 14.sp
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = KitBlue,
                                        selectedLabelColor = KitWhite
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (state.availableTimeSlots.isEmpty()) {
                            Text(
                                text = "Нет доступных слотов на этот день",
                                fontSize = 14.sp,
                                color = KitTextSecondary
                            )
                        } else {
                            // Time slot grid - FlowRow-like using rows
                            val chunked = state.availableTimeSlots.chunked(4)
                            chunked.forEach { row ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    row.forEach { time ->
                                        val isSelected = time == state.selectedTime
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { viewModel.onIntent(CheckoutIntent.SelectPickupTime(time)) },
                                            label = {
                                                Text(
                                                    text = time,
                                                    fontSize = 13.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = KitBlue,
                                                selectedLabelColor = KitWhite
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }

                    if (state.error != null) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.error!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                            // Если заказ создан, но платёж не прошёл — кнопка повтора
                            if (state.orderCreatedId != null && state.paymentUrl == null
                                && state.paymentMethod == PaymentMethod.CARD) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = { viewModel.onIntent(CheckoutIntent.RetryPayment) },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Повторить оплату", color = KitBlue)
                                }
                            }
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
                        enabled = !state.isLoading && state.items.isNotEmpty() && state.selectedTime != null
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
                    text = "${item.quantity} шт. x ${item.product.price.formatPrice()} \u20BD",
                    fontSize = 12.sp,
                    color = KitTextSecondary
                )
            }
            Text(
                text = "${(item.product.price * item.quantity).formatPrice()} \u20BD",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = KitTextPrimary
            )
        }
    }
}

private fun formatDateLabel(dateStr: String): String {
    return try {
        val date = kotlinx.datetime.LocalDate.parse(dateStr)
        val now = kotlinx.datetime.Clock.System.now()
            .toLocalDateTime(kotlinx.datetime.TimeZone.of("Asia/Vladivostok")).date
        val tomorrow = now.plus(1, kotlinx.datetime.DateTimeUnit.DAY)
        when (date) {
            now -> "Сегодня"
            tomorrow -> "Завтра"
            else -> {
                val months = listOf("", "янв", "фев", "мар", "апр", "мая", "июн", "июл", "авг", "сен", "окт", "ноя", "дек")
                "${date.dayOfMonth} ${months[date.monthNumber]}"
            }
        }
    } catch (_: Exception) {
        dateStr
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
