package com.example.kit_market.presentation.screen.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.koinInject
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.example.kit_market.domain.model.CartItem
import com.example.kit_market.presentation.common.QuantityCounter
import com.example.kit_market.presentation.common.formatPrice
import com.example.kit_market.presentation.screen.checkout.CheckoutScreen
import com.example.kit_market.presentation.screen.productdetail.ProductDetailScreen
import com.example.kit_market.presentation.theme.*

class CartScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinInject<CartViewModel>()
        val state by screenModel.state.collectAsState()

        Column(modifier = Modifier.fillMaxSize()) {
            // Title
            Text(
                text = "Корзина",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = KitTextPrimary,
                modifier = Modifier.padding(16.dp)
            )

            if (state.items.isEmpty() && !state.isLoading) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Корзина пуста",
                        fontSize = 16.sp,
                        color = KitTextSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.items, key = { it.product.id }) { item ->
                        CartItemCard(
                            item = item,
                            onIncrement = { screenModel.onIntent(CartIntent.Increment(item.product.id)) },
                            onDecrement = { screenModel.onIntent(CartIntent.Decrement(item.product.id)) },
                            onClick = { navigator.push(ProductDetailScreen(item.product.id)) }
                        )
                    }
                }
            }

            // Order button
            if (state.items.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
                    Button(
                        onClick = { navigator.push(CheckoutScreen()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KitBlue),
                        enabled = true
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Оформить заказ",
                                color = KitWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${state.totalPrice.formatPrice()} \u20BD",
                                color = KitWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.product.imageUrl.ifEmpty { null },
                contentDescription = item.product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = KitTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                QuantityCounter(
                    count = item.quantity,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement
                )
            }
            Text(
                text = "${(item.product.price * item.quantity).formatPrice()} \u20BD",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = KitTextPrimary
            )
        }
    }
}
